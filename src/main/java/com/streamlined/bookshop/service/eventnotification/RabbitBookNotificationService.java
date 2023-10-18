package com.streamlined.bookshop.service.eventnotification;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.config.messagebroker.outcomingevents.ModificationStatusRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.outcomingevents.QueryResultRabbitQueue;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.ModifyingOperationKind;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitBookNotificationService implements BookNotificationService {

	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void publishQueryResult(UUID requestId, List<BookDto> bookList, Instant instant, OperationStatus status) {
		try {
			QueryResultEvent event = new QueryResultEvent(requestId, bookList, instant, status);
			String content = objectMapper.writeValueAsString(event);
			Message message = new Message(content.getBytes());
			rabbitTemplate.send(QueryResultRabbitQueue.QUEUE_NAME, message);
		} catch (IOException e) {
			throw new EventNotificationException("cannot convert query result message to publish", e);
		}
	}

	@Override
	public void publishModificationStatus(UUID requestId, ModifyingOperationKind operation, Optional<BookDto> book,
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
