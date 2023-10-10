package com.streamlined.bookshop.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.config.QueryResultRabbitQueue;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitQueryResultMessageConsumer implements MessageConsumer {

	private final ObjectMapper objectMapper;

	@RabbitListener(queues = QueryResultRabbitQueue.QUEUE_NAME)
	@Override
	public void consumeMessage(Object obj) {
		Message message = (Message) obj;
		Type type = message.getMessageProperties().getInferredArgumentType();
		try {
			final String body = new String(message.getBody());
			List<?> queryResult = objectMapper.readValue(body, List.class);
			for (var r : queryResult) {
				System.out.println(r);
			}
		} catch (IOException e) {
			throw new ConsumerException("message cannot be consumed", e);
		}
	}

}
