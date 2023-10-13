package com.streamlined.bookshop.eventconsumer;

import java.io.IOException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.config.messagebroker.QueryResultRabbitQueue;
import com.streamlined.bookshop.service.notification.ModificationEvent;
import com.streamlined.bookshop.service.notification.QueryResultEvent;
import com.streamlined.bookshop.config.messagebroker.ModificationStatusRabbitQueue;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RabbitMessageConsumer implements MessageConsumer {

	private final ObjectMapper objectMapper;

	@RabbitListener(queues = QueryResultRabbitQueue.QUEUE_NAME)
	@Override
	public void consumeQueryResultMessage(Object obj) {
		Message message = (Message) obj;
		try {
			final String body = new String(message.getBody());
			final QueryResultEvent event = objectMapper.readValue(body, QueryResultEvent.class);
			System.out.println("%s %s".formatted(event.instant().toString(), event.status().toString()));
			for (var r : event.bookList()) {
				System.out.println(r);
			}
		} catch (IOException e) {
			throw new ConsumerException("query result message cannot be consumed", e);
		}
	}

	@RabbitListener(queues = ModificationStatusRabbitQueue.QUEUE_NAME)
	@Override
	public void consumeModificationStatusMessage(Object obj) {
		Message message = (Message) obj;
		try {
			final String body = new String(message.getBody());
			ModificationEvent modificationStatus = objectMapper.readValue(body, ModificationEvent.class);
			System.out.println(modificationStatus);
		} catch (IOException e) {
			throw new ConsumerException("modification status message cannot be consumed", e);
		}
	}

}
