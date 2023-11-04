package com.streamlined.bookshop.driver;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.exception.RequestProcessingException;
import com.streamlined.bookshop.exception.EventNotificationException;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.model.book.BookMapper;
import com.streamlined.bookshop.service.event.UpdateRequestEvent;
import com.streamlined.bookshop.service.event.AddRequestEvent;
import com.streamlined.bookshop.service.event.DeleteRequestEvent;
import com.streamlined.bookshop.service.event.Event;
import com.streamlined.bookshop.service.event.ModificationResponseEvent;
import com.streamlined.bookshop.service.event.QueryAllRequestEvent;
import com.streamlined.bookshop.service.event.QueryOneRequestEvent;
import com.streamlined.bookshop.service.event.QueryResultEvent;
import com.streamlined.bookshop.service.event.ResponseEvent;
import com.streamlined.bookshop.service.event.queue.ModificationRequestRabbitQueue;
import com.streamlined.bookshop.service.event.queue.ModificationStatusRabbitQueue;
import com.streamlined.bookshop.service.event.queue.QueryRequestRabbitQueue;
import com.streamlined.bookshop.service.event.queue.QueryResultRabbitQueue;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestGenerator {

	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;
	private final BookMapper bookMapper;

	private static final int INITIAL_DELAY_SECONDS = 1;
	private static final int FIXED_RATE_SECONDS = 10;
	private static final int QUEUE_INITIAL_CAPACITY = 1000;
	private final BlockingQueue<ResponseEvent> responseQueue = new ArrayBlockingQueue<>(QUEUE_INITIAL_CAPACITY);

	@Scheduled(initialDelay = INITIAL_DELAY_SECONDS, fixedRate = FIXED_RATE_SECONDS, timeUnit = TimeUnit.SECONDS)
	public void run() {
		publishQueryAllRequestEvent();
		ResponseEvent responseEvent;
		while ((responseEvent = responseQueue.poll()) != null) {
			if (responseEvent instanceof QueryResultEvent queryEvent && queryEvent.bookList().size() > 1) {
				for (BookDto book : queryEvent.bookList()) {
					publishQueryOneRequestEvent(book.id());
					publishUpdateRequestEvent(book, book.id());
					publishDeleteRequestEvent(book.id());
					var entity = bookMapper.toEntity(book);
					entity.setId(UUID.randomUUID());
					publishAddRequestEvent(bookMapper.toDto(entity));
				}
			}
		}

	}

	private void publishQueryAllRequestEvent() {
		try {
			UUID requestId = UUID.randomUUID();
			QueryAllRequestEvent event = new QueryAllRequestEvent(requestId, Instant.now());
			rabbitTemplate.send(QueryRequestRabbitQueue.QUEUE_NAME, getMessage(event));
		} catch (IOException e) {
			throw new EventNotificationException("cannot publish query all request message", e);
		}
	}

	private void publishQueryOneRequestEvent(UUID bookId) {
		try {
			UUID requestId = UUID.randomUUID();
			QueryOneRequestEvent event = new QueryOneRequestEvent(requestId, Instant.now(), bookId);
			rabbitTemplate.send(QueryRequestRabbitQueue.QUEUE_NAME, getMessage(event));
		} catch (IOException e) {
			throw new EventNotificationException("cannot publish query one request message", e);
		}
	}

	private void publishUpdateRequestEvent(BookDto book, UUID bookId) {
		try {
			UUID requestId = UUID.randomUUID();
			UpdateRequestEvent event = new UpdateRequestEvent(requestId, Instant.now(), book, bookId);
			rabbitTemplate.send(ModificationRequestRabbitQueue.QUEUE_NAME, getMessage(event));
		} catch (IOException e) {
			throw new EventNotificationException("cannot publish update request message", e);
		}
	}

	private void publishDeleteRequestEvent(UUID bookId) {
		try {
			UUID requestId = UUID.randomUUID();
			DeleteRequestEvent event = new DeleteRequestEvent(requestId, Instant.now(), bookId);
			rabbitTemplate.send(ModificationRequestRabbitQueue.QUEUE_NAME, getMessage(event));
		} catch (IOException e) {
			throw new EventNotificationException("cannot publish delete request message", e);
		}
	}

	private void publishAddRequestEvent(BookDto book) {
		try {
			UUID requestId = UUID.randomUUID();
			AddRequestEvent event = new AddRequestEvent(requestId, Instant.now(), book);
			rabbitTemplate.send(ModificationRequestRabbitQueue.QUEUE_NAME, getMessage(event));
		} catch (IOException e) {
			throw new EventNotificationException("cannot publish add request message", e);
		}
	}

	private Message getMessage(Event event) throws JsonProcessingException {
		String content = objectMapper.writeValueAsString(event);
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setType(event.getClass().getName());
		return new Message(content.getBytes(), messageProperties);
	}

	@RabbitListener(queues = QueryResultRabbitQueue.QUEUE_NAME)
	public void consumeQueryResultMessage(Message message) {
		try {
			String body = new String(message.getBody());
			QueryResultEvent event = objectMapper.readValue(body, QueryResultEvent.class);
			responseQueue.add(event);
			System.out.println(event);
		} catch (IOException e) {
			throw new RequestProcessingException("query result message cannot be consumed", e);
		}
	}

	@RabbitListener(queues = ModificationStatusRabbitQueue.QUEUE_NAME)
	public void consumeModificationStatusMessage(Message message) {
		try {
			String body = new String(message.getBody());
			ModificationResponseEvent event = objectMapper.readValue(body, ModificationResponseEvent.class);
			responseQueue.add(event);
			System.out.println(event);
		} catch (IOException e) {
			throw new RequestProcessingException("modification status message cannot be consumed", e);
		}
	}

}
