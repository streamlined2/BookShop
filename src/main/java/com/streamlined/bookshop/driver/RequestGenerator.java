package com.streamlined.bookshop.driver;

import java.io.IOException;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.exception.RequestProcessingException;
import com.streamlined.bookshop.exception.EventNotificationException;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.model.book.BookMapper;
import com.streamlined.bookshop.service.event.UpdateBookRequestEvent;
import com.streamlined.bookshop.service.event.AddBookRequestEvent;
import com.streamlined.bookshop.service.event.DeleteBookRequestEvent;
import com.streamlined.bookshop.service.event.Event;
import com.streamlined.bookshop.service.event.ModificationResponseEvent;
import com.streamlined.bookshop.service.event.QueryAllBookRequestEvent;
import com.streamlined.bookshop.service.event.QueryOneBookRequestEvent;
import com.streamlined.bookshop.service.event.QueryResultEvent;
import com.streamlined.bookshop.service.event.ResponseEvent;
import static com.streamlined.bookshop.config.messagebroker.RabbitConfig.Service;
import static com.streamlined.bookshop.config.messagebroker.RabbitConfig.Kind;
import static com.streamlined.bookshop.config.messagebroker.RabbitConfig.Type;
import static com.streamlined.bookshop.config.messagebroker.RabbitConfig.getRoutingKey;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestGenerator {

	private final RabbitTemplate rabbitTemplate;
	private final DirectExchange exchange;
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
			if (responseEvent instanceof QueryResultEvent qEvent) {
				for (BookDto book : ((QueryResultEvent<BookDto>) qEvent).getList()) {
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
			QueryAllBookRequestEvent event = new QueryAllBookRequestEvent();
			rabbitTemplate.send(exchange.getName(), getRoutingKey(Service.BOOK_SERVICE, Kind.QUERY, Type.REQUEST),
					getMessage(event));
		} catch (IOException e) {
			throw new EventNotificationException("cannot publish query all request message", e);
		}
	}

	private void publishQueryOneRequestEvent(UUID bookId) {
		try {
			QueryOneBookRequestEvent event = new QueryOneBookRequestEvent(bookId);
			rabbitTemplate.send(exchange.getName(), getRoutingKey(Service.BOOK_SERVICE, Kind.QUERY, Type.REQUEST),
					getMessage(event));
		} catch (IOException e) {
			throw new EventNotificationException("cannot publish query one request message", e);
		}
	}

	private void publishUpdateRequestEvent(BookDto book, UUID bookId) {
		try {
			UpdateBookRequestEvent event = new UpdateBookRequestEvent(book, bookId);
			rabbitTemplate.send(exchange.getName(),
					getRoutingKey(Service.BOOK_SERVICE, Kind.MODIFICATION, Type.REQUEST), getMessage(event));
		} catch (IOException e) {
			throw new EventNotificationException("cannot publish update request message", e);
		}
	}

	private void publishDeleteRequestEvent(UUID bookId) {
		try {
			DeleteBookRequestEvent event = new DeleteBookRequestEvent(bookId);
			rabbitTemplate.send(exchange.getName(),
					getRoutingKey(Service.BOOK_SERVICE, Kind.MODIFICATION, Type.REQUEST), getMessage(event));
		} catch (IOException e) {
			throw new EventNotificationException("cannot publish delete request message", e);
		}
	}

	private void publishAddRequestEvent(BookDto book) {
		try {
			AddBookRequestEvent event = new AddBookRequestEvent(book);
			rabbitTemplate.send(exchange.getName(),
					getRoutingKey(Service.BOOK_SERVICE, Kind.MODIFICATION, Type.REQUEST), getMessage(event));
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

	@RabbitListener(queues = "#{bookServiceQueryResponseQueue}")
	public void consumeQueryResultMessage(Message message) {
		try {
			String body = new String(message.getBody());
			QueryResultEvent<BookDto> event = objectMapper.readValue(body,
					new TypeReference<QueryResultEvent<BookDto>>() {
					});
			responseQueue.add(event);
			System.out.println(event);
		} catch (IOException e) {
			throw new RequestProcessingException("query result message cannot be consumed", e);
		}
	}

	@RabbitListener(queues = "#{bookServiceModificationResponseQueue}")
	public void consumeModificationStatusMessage(Message message) {
		try {
			String body = new String(message.getBody());
			ModificationResponseEvent<BookDto> event = objectMapper.readValue(body,
					new TypeReference<ModificationResponseEvent<BookDto>>() {
					});
			responseQueue.add(event);
			System.out.println(event);
		} catch (IOException e) {
			throw new RequestProcessingException("modification status message cannot be consumed", e);
		}
	}

}
