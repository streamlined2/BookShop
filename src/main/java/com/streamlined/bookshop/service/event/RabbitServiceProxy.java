package com.streamlined.bookshop.service.event;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.exception.RequestProcessingException;

import lombok.RequiredArgsConstructor;

import com.streamlined.bookshop.exception.OperationFailedException;
import static com.streamlined.bookshop.config.messagebroker.RabbitConfig.Service;
import static com.streamlined.bookshop.config.messagebroker.RabbitConfig.Kind;
import static com.streamlined.bookshop.config.messagebroker.RabbitConfig.Type;
import static com.streamlined.bookshop.config.messagebroker.RabbitConfig.getRoutingKey;

@RequiredArgsConstructor
public abstract class RabbitServiceProxy<D, S> {

	private final S service;
	private final ObjectMapper objectMapper;
	private final RabbitTemplate rabbitTemplate;
	private final DirectExchange exchange;
	private final Service serviceType;

	@RabbitListener(queues = "#{bookServiceQueryRequestQueue}")
	public void processQueryRequest(Message message) {
		try {
			String body = new String(message.getBody());
			Class<? extends QueryRequestEvent<D, S>> valueType = getMessageType(message);
			QueryRequestEvent<D, S> event = objectMapper.readValue(body, valueType);
			executeQueryAndPublishResult(event);
		} catch (Exception e) {
			throw new RequestProcessingException("query request message cannot be processed", e);
		}
	}

	private void executeQueryAndPublishResult(QueryRequestEvent<D, S> event) {
		try {
			List<D> queryResult = executeQueryRequest(event);
			publishQueryResult(event, queryResult, OperationStatus.SUCCESS);
		} catch (OperationFailedException e) {
			publishQueryResult(event, List.of(), OperationStatus.FAILURE);
		}
	}

	private List<D> executeQueryRequest(QueryRequestEvent<D, S> event) {
		try {
			return event.executeQuery(service);
		} catch (Exception e) {
			throw new OperationFailedException("operation %s raised exception".formatted(event.toString()), e);
		}
	}

	private void publishQueryResult(QueryRequestEvent<D, S> requestEvent, List<D> list, OperationStatus status) {
		try {
			QueryResultEvent<D> resultEvent = new QueryResultEvent<>(list, requestEvent.getRequestId(), status);
			rabbitTemplate.send(exchange.getName(), getRoutingKey(serviceType, Kind.QUERY, Type.RESPONSE),
					getMessage(resultEvent));
		} catch (IOException e) {
			throw new RequestProcessingException("cannot publish query result message", e);
		}
	}

	@RabbitListener(queues = "#{bookServiceModificationRequestQueue}")
	public void processModificationRequest(Message message) {
		try {
			String body = new String(message.getBody());
			Class<? extends ModificationRequestEvent<D, S>> valueType = getMessageType(message);
			ModificationRequestEvent<D, S> event = objectMapper.readValue(body, valueType);
			executeModificationAndPublishStatus(event);
		} catch (Exception e) {
			throw new RequestProcessingException("modification status message cannot be processed", e);
		}
	}

	private <T> Class<T> getMessageType(Message message) throws ClassNotFoundException {
		return (Class<T>) Class.forName(message.getMessageProperties().getType());
	}

	private void executeModificationAndPublishStatus(ModificationRequestEvent<D, S> event) {
		try {
			Optional<D> updatedDto = executeModificationRequest(event);
			publishModificationStatus(event, updatedDto, OperationStatus.SUCCESS);
		} catch (OperationFailedException e) {
			publishModificationStatus(event, Optional.empty(), OperationStatus.FAILURE);
		}
	}

	private Optional<D> executeModificationRequest(ModificationRequestEvent<D, S> event) {
		try {
			return event.executeUpdate(service);
		} catch (Exception e) {
			throw new OperationFailedException("operation %s raised exception".formatted(event.toString()), e);
		}
	}

	private void publishModificationStatus(ModificationRequestEvent<D, S> requestEvent, Optional<D> dto,
			OperationStatus status) {
		try {
			ModificationResponseEvent<D> responseEvent = new ModificationResponseEvent<>(dto.orElse(null),
					requestEvent.getRequestId(), status);
			rabbitTemplate.send(exchange.getName(), getRoutingKey(serviceType, Kind.MODIFICATION, Type.RESPONSE),
					getMessage(responseEvent));
		} catch (IOException e) {
			throw new RequestProcessingException("cannot publish modification status message", e);
		}
	}

	private Message getMessage(Event event) throws JsonProcessingException {
		String content = objectMapper.writeValueAsString(event);
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setType(event.getClass().getName());
		return new Message(content.getBytes(), messageProperties);
	}

}
