package com.streamlined.bookshop.service.notification;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.config.messagebroker.ModificationStatusRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.QueryResultRabbitQueue;
import com.streamlined.bookshop.model.BookDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitBookNotificationService implements BookNotificationService {

	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void publishQueryResult(List<BookDto> bookList, Instant instant, OperationStatus status) {
		try {
			final QueryResultEvent event = new QueryResultEvent(bookList, instant, status);
			final String content = objectMapper.writeValueAsString(event);
			final Message message = new Message(content.getBytes());
			rabbitTemplate.send(QueryResultRabbitQueue.QUEUE_NAME, message);
		} catch (IOException e) {
			throw new NotificationException("cannot convert query result message to publish", e);
		}
	}

	@Override
	public void publishModificationStatus(OperationKind operation, Optional<BookDto> book, Instant instant,
			OperationStatus status) {
		try {
			final ModificationEvent event = new ModificationEvent(operation, book.orElse(null), instant, status);
			final String content = objectMapper.writeValueAsString(event);
			final Message message = new Message(content.getBytes());
			rabbitTemplate.send(ModificationStatusRabbitQueue.QUEUE_NAME, message);
		} catch (IOException e) {
			throw new NotificationException("cannot convert modification status message to publish", e);
		}
	}

}
