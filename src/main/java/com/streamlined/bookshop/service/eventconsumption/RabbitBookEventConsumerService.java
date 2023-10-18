package com.streamlined.bookshop.service.eventconsumption;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.config.messagebroker.incomingevents.ModificationRequestRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.incomingevents.QueryRequestRabbitQueue;
import com.streamlined.bookshop.exception.ConsumerException;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.ModifyingOperationKind;
import com.streamlined.bookshop.service.book.BookService;
import com.streamlined.bookshop.service.eventnotification.BookNotificationService;
import com.streamlined.bookshop.service.eventnotification.OperationStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitBookEventConsumerService implements BookEventConsumerService {

	private final BookService bookService;
	private final ObjectMapper objectMapper;
	private final List<BookNotificationService> notificationServiceList;

	@RabbitListener(queues = QueryRequestRabbitQueue.QUEUE_NAME)
	@Override
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
			publishQueryResultMessage(event.requestId(), bookList);
		}
		case QUERY_ONE_BY_ID -> {
			UUID id = UUID.fromString((String) event.params()[0]);
			Optional<BookDto> dto = bookService.getBook(id);
			dto.ifPresentOrElse(book -> publishQueryResultMessage(event.requestId(), List.of(book)),
					() -> publishQueryResultMessage(event.requestId(), List.of()));
		}
		default -> throw new ConsumerException("wrong operation kind for the event %s".formatted(event.toString()));
		}
	}

	private void publishQueryResultMessage(UUID requestId, List<BookDto> bookList) {
		notificationServiceList.forEach(
				service -> service.publishQueryResult(requestId, bookList, Instant.now(), OperationStatus.SUCCESS));
	}

	@RabbitListener(queues = ModificationRequestRabbitQueue.QUEUE_NAME)
	@Override
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
			publishModificationStatusMessage(requestId, ModifyingOperationKind.UPDATE, updatedBook, Instant.now(),
					OperationStatus.SUCCESS);
		}
		case DELETE -> {
			UUID id = UUID.fromString((String) event.params()[0]);
			var book = bookService.deleteBook(id);
			publishModificationStatusMessage(requestId, ModifyingOperationKind.DELETE, book, Instant.now(),
					OperationStatus.SUCCESS);
		}
		case ADD -> {
			BookDto book = objectMapper.convertValue((event.params()[0]), BookDto.class);
			var newBook = bookService.addBook(book);
			publishModificationStatusMessage(requestId, ModifyingOperationKind.ADD, newBook, Instant.now(),
					OperationStatus.SUCCESS);
		}
		default -> throw new ConsumerException("wrong operation kind for the event %s".formatted(event.toString()));
		}
	}

	private void publishModificationStatusMessage(UUID requestId, ModifyingOperationKind operation,
			Optional<BookDto> book, Instant instant, OperationStatus status) {
		notificationServiceList
				.forEach(service -> service.publishModificationStatus(requestId, operation, book, instant, status));
	}

}
