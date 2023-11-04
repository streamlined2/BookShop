package com.streamlined.bookshop.service.eventconsumption;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.config.messagebroker.incomingevents.ModificationRequestRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.incomingevents.QueryRequestRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.outcomingevents.ModificationStatusRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.outcomingevents.QueryResultRabbitQueue;
import com.streamlined.bookshop.exception.RequestProcessingException;
import com.streamlined.bookshop.exception.OperationFailedException;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;
import com.streamlined.bookshop.service.eventnotification.ModificationResponseEvent;
import com.streamlined.bookshop.service.eventnotification.OperationStatus;
import com.streamlined.bookshop.service.eventnotification.QueryResultEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RabbitBookServiceProxy {

	private final BookService bookService;
	private final ObjectMapper objectMapper;
	private final RabbitTemplate rabbitTemplate;

	@RabbitListener(queues = QueryRequestRabbitQueue.QUEUE_NAME)
	public void processQueryRequest(Message message) {
		try {
			String body = new String(message.getBody());
			QueryRequestEvent event = objectMapper.readValue(body, new TypeReference<QueryRequestEvent>() {
			});
			executeQueryAndPublishResult(event);
		} catch (Exception e) {
			throw new RequestProcessingException("query request message cannot be processed", e);
		}
	}

	private void executeQueryAndPublishResult(QueryRequestEvent event) {
		try {
			List<BookDto> queryResult = dispatchExecuteQueryRequest(event);
			publishQueryResult(event, queryResult, Instant.now(), OperationStatus.SUCCESS);
		} catch (OperationFailedException e) {
			publishQueryResult(event, List.of(), Instant.now(), OperationStatus.FAILURE);
		}
	}

	private List<BookDto> dispatchExecuteQueryRequest(QueryRequestEvent event) {
		try {
			return event.kind().executeQuery(bookService, event.params());
		} catch (Exception e) {
			throw new OperationFailedException("operation %s raised exception".formatted(event.kind().toString()), e);
		}
	}

	private void publishQueryResult(QueryRequestEvent requestEvent, List<BookDto> bookList, Instant instant,
			OperationStatus status) {
		try {
			QueryResultEvent resultEvent = new QueryResultEvent(requestEvent.requestId(), bookList, instant, status);
			String content = objectMapper.writeValueAsString(resultEvent);
			Message message = new Message(content.getBytes());
			rabbitTemplate.send(QueryResultRabbitQueue.QUEUE_NAME, message);
		} catch (IOException e) {
			throw new RequestProcessingException("cannot convert query result message to publish", e);
		}
	}

	@RabbitListener(queues = ModificationRequestRabbitQueue.QUEUE_NAME)
	public void processModificationRequest(Message message) {
		try {
			String body = new String(message.getBody());
			ModificationRequestEvent event = objectMapper.readValue(body,
					new TypeReference<ModificationRequestEvent>() {
					});
			executeModificationAndPublishStatus(event);
		} catch (Exception e) {
			throw new RequestProcessingException("modification status message cannot be processed", e);
		}
	}

	private void executeModificationAndPublishStatus(ModificationRequestEvent event) {
		try {
			Optional<BookDto> updatedBook = dispatchExecuteModificationRequest(event);
			publishModificationStatus(event, updatedBook, Instant.now(), OperationStatus.SUCCESS);
		} catch (OperationFailedException e) {
			publishModificationStatus(event, Optional.empty(), Instant.now(), OperationStatus.FAILURE);
		}
	}

	private Optional<BookDto> dispatchExecuteModificationRequest(ModificationRequestEvent event) {
		try {
			return event.kind().executeUpdate(bookService, objectMapper, event.params());
		} catch (Exception e) {
			throw new OperationFailedException("operation %s raised exception".formatted(event.kind().toString()), e);
		}
	}

	private void publishModificationStatus(ModificationRequestEvent requestEvent, Optional<BookDto> book,
			Instant instant, OperationStatus status) {
		try {
			ModificationResponseEvent responseEvent = new ModificationResponseEvent(requestEvent.requestId(),
					requestEvent.kind(), book.orElse(null), instant, status);
			String content = objectMapper.writeValueAsString(responseEvent);
			Message message = new Message(content.getBytes());
			rabbitTemplate.send(ModificationStatusRabbitQueue.QUEUE_NAME, message);
		} catch (IOException e) {
			throw new RequestProcessingException("cannot convert modification status message to publish", e);
		}
	}

}
