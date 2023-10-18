package com.streamlined.bookshop.driver;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.config.messagebroker.incomingevents.ModificationRequestRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.incomingevents.QueryRequestRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.outcomingevents.ModificationStatusRabbitQueue;
import com.streamlined.bookshop.config.messagebroker.outcomingevents.QueryResultRabbitQueue;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.model.book.BookMapper;
import com.streamlined.bookshop.service.ModifyingOperationKind;
import com.streamlined.bookshop.service.QueryingOperationKind;
import com.streamlined.bookshop.service.eventconsumption.ModificationRequestEvent;
import com.streamlined.bookshop.service.eventconsumption.QueryRequestEvent;
import com.streamlined.bookshop.service.eventnotification.EventNotificationException;
import com.streamlined.bookshop.service.eventnotification.ModificationResponseEvent;
import com.streamlined.bookshop.service.eventnotification.QueryResultEvent;
import com.streamlined.bookshop.service.eventnotification.ResponseEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestGenerator implements MessageConsumer {

	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;
	private final BookMapper bookMapper;

	private static final int FIXED_RATE_SECONDS = 5;
	private static final int QUEUE_INITIAL_CAPACITY = 1000;
	private final BlockingQueue<ResponseEvent> responseQueue = new ArrayBlockingQueue<>(QUEUE_INITIAL_CAPACITY);

	@Scheduled(fixedRate = FIXED_RATE_SECONDS, timeUnit = TimeUnit.SECONDS)
	public void run() {
		publishQueryRequestEvent(QueryingOperationKind.QUERY_ALL);
		ResponseEvent responseEvent;
		while ((responseEvent = responseQueue.poll()) != null) {
			if (responseEvent instanceof QueryResultEvent queryEvent) {
				for (BookDto book : queryEvent.bookList()) {
					publishQueryRequestEvent(QueryingOperationKind.QUERY_ONE_BY_ID, book.id());
					publishModificationRequestEvent(ModifyingOperationKind.UPDATE, book, book.id());
					publishModificationRequestEvent(ModifyingOperationKind.DELETE, book.id());
					var entity = bookMapper.toEntity(book);
					entity.setId(UUID.randomUUID());
					publishModificationRequestEvent(ModifyingOperationKind.ADD, bookMapper.toDto(entity));
				}
			}
		}
	}

	public void publishQueryRequestEvent(QueryingOperationKind operationKind, Object... params) {
		try {
			UUID requestId = UUID.randomUUID();
			QueryRequestEvent event = new QueryRequestEvent(requestId, operationKind, params);
			String content = objectMapper.writeValueAsString(event);
			Message message = new Message(content.getBytes());
			rabbitTemplate.send(QueryRequestRabbitQueue.QUEUE_NAME, message);
		} catch (IOException e) {
			throw new EventNotificationException("cannot convert query request message to publish", e);
		}
	}

	public void publishModificationRequestEvent(ModifyingOperationKind operationKind, Object... params) {
		try {
			UUID requestId = UUID.randomUUID();
			ModificationRequestEvent event = new ModificationRequestEvent(requestId, operationKind, params);
			String content = objectMapper.writeValueAsString(event);
			Message message = new Message(content.getBytes());
			rabbitTemplate.send(ModificationRequestRabbitQueue.QUEUE_NAME, message);
		} catch (IOException e) {
			throw new EventNotificationException("cannot convert modification request message to publish", e);
		}
	}

	@RabbitListener(queues = QueryResultRabbitQueue.QUEUE_NAME)
	@Override
	public void consumeQueryResultMessage(Message message) {
		try {
			String body = new String(message.getBody());
			QueryResultEvent event = objectMapper.readValue(body, QueryResultEvent.class);
			responseQueue.add(event);
			System.out.println(event);
		} catch (IOException e) {
			throw new ConsumerException("query result message cannot be consumed", e);
		}
	}

	@RabbitListener(queues = ModificationStatusRabbitQueue.QUEUE_NAME)
	@Override
	public void consumeModificationStatusMessage(Message message) {
		try {
			String body = new String(message.getBody());
			ModificationResponseEvent event = objectMapper.readValue(body, ModificationResponseEvent.class);
			responseQueue.add(event);
			System.out.println(event);
		} catch (IOException e) {
			throw new ConsumerException("modification status message cannot be consumed", e);
		}
	}

}
