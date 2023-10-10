package com.streamlined.bookshop.service;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.config.QueryResultRabbitQueue;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitNotificationService implements NotificationService {

	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void publishQueryResult(Object obj) {
		try {
			final String content = objectMapper.writeValueAsString(obj);
			Message message = new Message(content.getBytes());
			rabbitTemplate.send(QueryResultRabbitQueue.QUEUE_NAME, message);
		} catch (IOException e) {
			throw new NotificationException("cannot convert message to publish", e);
		}
	}

}
