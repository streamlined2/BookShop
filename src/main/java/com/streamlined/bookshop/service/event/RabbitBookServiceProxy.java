package com.streamlined.bookshop.service.event;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.exception.RequestProcessingException;
import com.streamlined.bookshop.exception.OperationFailedException;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;
import com.streamlined.bookshop.service.event.queue.ModificationRequestRabbitQueue;
import com.streamlined.bookshop.service.event.queue.ModificationStatusRabbitQueue;
import com.streamlined.bookshop.service.event.queue.QueryRequestRabbitQueue;
import com.streamlined.bookshop.service.event.queue.QueryResultRabbitQueue;

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
			Class<? extends QueryRequestEvent> valueType = getMessageType(message);
			QueryRequestEvent event = objectMapper.readValue(body, valueType);
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
			return event.executeQuery(bookService);
		} catch (Exception e) {
			throw new OperationFailedException("operation %s raised exception".formatted(event.toString()), e);
		}
	}

	private void publishQueryResult(QueryRequestEvent requestEvent, List<BookDto> bookList, Instant instant,
			OperationStatus status) {
		try {
			QueryResultEvent resultEvent = new QueryResultEvent(requestEvent.requestId(), instant, bookList, status);
			rabbitTemplate.send(QueryResultRabbitQueue.QUEUE_NAME, getMessage(resultEvent));
		} catch (IOException e) {
			throw new RequestProcessingException("cannot publish query result message", e);
		}
	}

	@RabbitListener(queues = ModificationRequestRabbitQueue.QUEUE_NAME)
	public void processModificationRequest(Message message) {
		try {
			String body = new String(message.getBody());
			Class<? extends ModificationRequestEvent> valueType = getMessageType(message);
			ModificationRequestEvent event = objectMapper.readValue(body, valueType);
			executeModificationAndPublishStatus(event);
		} catch (Exception e) {
			throw new RequestProcessingException("modification status message cannot be processed", e);
		}
	}

	private <T> Class<T> getMessageType(Message message) throws ClassNotFoundException {
		return (Class<T>) Class.forName(message.getMessageProperties().getType());
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
			return event.executeUpdate(bookService);
		} catch (Exception e) {
			throw new OperationFailedException("operation %s raised exception".formatted(event.toString()), e);
		}
	}

	private void publishModificationStatus(ModificationRequestEvent requestEvent, Optional<BookDto> book,
			Instant instant, OperationStatus status) {
		try {
			ModificationResponseEvent responseEvent = new ModificationResponseEvent(requestEvent.requestId(),
					book.orElse(null), instant, status);
			rabbitTemplate.send(ModificationStatusRabbitQueue.QUEUE_NAME, getMessage(responseEvent));
		} catch (IOException e) {
			throw new RequestProcessingException("cannot publish modification status message", e);
		}
	}

	private Message getMessage(Event event) throws JsonProcessingException {
		String content = objectMapper.writeValueAsString(event);
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setType(event.getClass().getName());
		return new Message(content.getBytes(), messageProperties);
	}

}
