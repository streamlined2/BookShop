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
			dispatchQueryRequest(event);
		} catch (IOException e) {
			throw new ConsumerException("query request message cannot be consumed", e);
		}
	}

	private void dispatchQueryRequest(QueryRequestEvent event) {
		switch (event.kind()) {
		case QUERY_ALL -> {
			List<BookDto> bookList = bookService.getAllBooks();
			publishQueryResult(event.requestId(), bookList, Instant.now(), OperationStatus.SUCCESS);
		}
		case QUERY_ONE_BY_ID -> {
			UUID id = UUID.fromString((String) event.params()[0]);
			Optional<BookDto> dto = bookService.getBook(id);
			dto.ifPresentOrElse(
					book -> publishQueryResult(event.requestId(), List.of(book), Instant.now(),
							OperationStatus.SUCCESS),
					() -> publishQueryResult(event.requestId(), List.of(), Instant.now(), OperationStatus.SUCCESS));
		}
		default -> throw new ConsumerException("wrong operation kind for the event %s".formatted(event.toString()));
		}
	}

	private void publishQueryResult(UUID requestId, List<BookDto> bookList, Instant instant, OperationStatus status) {
		try {
			QueryResultEvent event = new QueryResultEvent(requestId, bookList, instant, status);
			String content = objectMapper.writeValueAsString(event);
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
			dispatchModificationRequest(event);
		} catch (IOException e) {
			throw new ConsumerException("modification status message cannot be consumed", e);
		}
	}

	private void dispatchModificationRequest(ModificationRequestEvent event) {
		UUID requestId = event.requestId();
		switch (event.kind()) {
		case UPDATE -> {
			BookDto book = objectMapper.convertValue((event.params()[0]), BookDto.class);
			UUID id = UUID.fromString((String) event.params()[1]);
			var updatedBook = bookService.updateBook(book, id);
			publishModificationStatus(requestId, ModifyingOperationKind.UPDATE, updatedBook, Instant.now(),
					OperationStatus.SUCCESS);
		}
		case DELETE -> {
			UUID id = UUID.fromString((String) event.params()[0]);
			var book = bookService.deleteBook(id);
			publishModificationStatus(requestId, ModifyingOperationKind.DELETE, book, Instant.now(),
					OperationStatus.SUCCESS);
		}
		case ADD -> {
			BookDto book = objectMapper.convertValue((event.params()[0]), BookDto.class);
			var newBook = bookService.addBook(book);
			publishModificationStatus(requestId, ModifyingOperationKind.ADD, newBook, Instant.now(),
					OperationStatus.SUCCESS);
		}
		default -> throw new ConsumerException("wrong operation kind for the event %s".formatted(event.toString()));
		}
	}

	private void publishModificationStatus(UUID requestId, ModifyingOperationKind operation, Optional<BookDto> book,
			Instant instant, OperationStatus status) {
		try {
			ModificationResponseEvent event = new ModificationResponseEvent(requestId, operation, book.orElse(null),
					instant, status);
			String content = objectMapper.writeValueAsString(event);
			Message message = new Message(content.getBytes());
			rabbitTemplate.send(ModificationStatusRabbitQueue.QUEUE_NAME, message);
		} catch (IOException e) {
			throw new EventNotificationException("cannot convert modification status message to publish", e);
		}
	}

}
