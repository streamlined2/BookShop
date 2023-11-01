package com.streamlined.bookshop.service.eventconsumption;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.config.messagebroker.incomingevents.ModificationRequestRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.incomingevents.QueryRequestRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.outcomingevents.ModificationStatusRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.outcomingevents.QueryResultRabbitQueue;
import com.streamlined.bookshop.exception.ConsumerException;
import com.streamlined.bookshop.exception.EventNotificationException;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.ModifyingOperationKind;
import com.streamlined.bookshop.service.book.BookService;
import com.streamlined.bookshop.service.eventnotification.ModificationResponseEvent;
import com.streamlined.bookshop.service.eventnotification.OperationStatus;
import com.streamlined.bookshop.service.eventnotification.QueryResultEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitBookServiceProxy {

	private final BookService bookService;
	private final ObjectMapper objectMapper;
	private final RabbitTemplate rabbitTemplate;

	@RabbitListener(queues = QueryRequestRabbitQueue.QUEUE_NAME)
	public void consumeQueryRequest(Message message) {
		try {
			String body = new String(message.getBody());
			QueryRequestEvent event = objectMapper.readValue(body, new TypeReference<QueryRequestEvent>() {
			});
			List<BookDto> queryResult = dispatchQueryRequest(event);
			publishQueryResult(event, queryResult, Instant.now(), OperationStatus.SUCCESS);
		} catch (IOException e) {
			throw new ConsumerException("query request message cannot be consumed", e);
		}
	}

	private List<BookDto> dispatchQueryRequest(QueryRequestEvent event) {
		return switch (event.kind()) {
		case QUERY_ALL -> bookService.getAllBooks();
		case QUERY_ONE_BY_ID -> {
			UUID id = UUID.fromString((String) event.params()[0]);
			Optional<BookDto> dto = bookService.getBook(id);
			yield dto.map(List::of).orElse(List.of());
		}
		default -> throw new ConsumerException("wrong operation kind for the event %s".formatted(event.toString()));
		};
	}

	private void publishQueryResult(QueryRequestEvent requestEvent, List<BookDto> bookList, Instant instant,
			OperationStatus status) {
		try {
			QueryResultEvent resultEvent = new QueryResultEvent(requestEvent.requestId(), bookList, instant, status);
			String content = objectMapper.writeValueAsString(resultEvent);
			Message message = new Message(content.getBytes());
			rabbitTemplate.send(QueryResultRabbitQueue.QUEUE_NAME, message);
		} catch (IOException e) {
			throw new EventNotificationException("cannot convert query result message to publish", e);
		}
	}

	@RabbitListener(queues = ModificationRequestRabbitQueue.QUEUE_NAME)
	public void consumeModificationRequest(Message message) {
		try {
			String body = new String(message.getBody());
			ModificationRequestEvent event = objectMapper.readValue(body,
					new TypeReference<ModificationRequestEvent>() {
					});
			Optional<BookDto> updatedBook = dispatchModificationRequest(event);
			publishModificationStatus(event, updatedBook, Instant.now(), OperationStatus.SUCCESS);
		} catch (IOException e) {
			throw new ConsumerException("modification status message cannot be consumed", e);
		}
	}

	private Optional<BookDto> dispatchModificationRequest(ModificationRequestEvent event) {
		return switch (event.kind()) {
		case UPDATE -> {
			BookDto book = objectMapper.convertValue((event.params()[0]), BookDto.class);
			UUID id = UUID.fromString((String) event.params()[1]);
			yield bookService.updateBook(book, id);
		}
		case DELETE -> {
			UUID id = UUID.fromString((String) event.params()[0]);
			yield bookService.deleteBook(id);
		}
		case ADD -> {
			BookDto book = objectMapper.convertValue((event.params()[0]), BookDto.class);
			yield bookService.addBook(book);
		}
		default -> throw new ConsumerException("wrong operation kind for the event %s".formatted(event.toString()));
		};
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
			throw new EventNotificationException("cannot convert modification status message to publish", e);
		}
	}

}
