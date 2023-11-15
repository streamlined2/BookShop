package com.streamlined.bookshop.config.messagebroker;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

	public enum Service {
		BOOK_SERVICE
	}

	public enum Kind {
		QUERY, MODIFICATION
	}

	public enum Type {
		REQUEST, RESPONSE
	}

	public static String getRoutingKey(Service service, Kind kind, Type type) {
		return "%s:%s:%s".formatted(service.toString(), kind.toString(), type.toString());
	}

	@Bean
	DirectExchange exchange() {
		return new DirectExchange("SERVICE_EXCHANGE");
	}

	@Bean
	Queue bookServiceQueryRequestQueue() {
		return new Queue("BOOK_SERVICE_QUERY_REQUEST_QUEUE");
	}

	@Bean
	Binding bookServiceQueryRequestBinding() {
		return BindingBuilder.bind(bookServiceQueryRequestQueue()).to(exchange())
				.with(getRoutingKey(Service.BOOK_SERVICE, Kind.QUERY, Type.REQUEST));
	}

	@Bean
	Queue bookServiceModificationRequestQueue() {
		return new Queue("BOOK_SERVICE_MODIFICATION_REQUEST_QUEUE");
	}

	@Bean
	Binding bookServiceModificationRequestBinding() {
		return BindingBuilder.bind(bookServiceModificationRequestQueue()).to(exchange())
				.with(getRoutingKey(Service.BOOK_SERVICE, Kind.MODIFICATION, Type.REQUEST));
	}

	@Bean
	Queue bookServiceQueryResponseQueue() {
		return new Queue("BOOK_SERVICE_QUERY_RESPONSE_QUEUE");
	}

	@Bean
	Binding bookServiceQueryResponseBinding() {
		return BindingBuilder.bind(bookServiceQueryResponseQueue()).to(exchange())
				.with(getRoutingKey(Service.BOOK_SERVICE, Kind.QUERY, Type.RESPONSE));
	}

	@Bean
	Queue bookServiceModificationResponseQueue() {
		return new Queue("BOOK_SERVICE_MODIFICATION_RESPONSE_QUEUE");
	}

	@Bean
	Binding bookServiceModificationResponseBinding() {
		return BindingBuilder.bind(bookServiceModificationResponseQueue()).to(exchange())
				.with(getRoutingKey(Service.BOOK_SERVICE, Kind.MODIFICATION, Type.RESPONSE));
	}

}
