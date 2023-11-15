package com.streamlined.bookshop.service.event;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.config.messagebroker.RabbitConfig;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

@Component
public class RabbitBookServiceProxy extends RabbitServiceProxy<BookDto, BookService> {

	public RabbitBookServiceProxy(BookService bookService, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate,
			DirectExchange exchange) {
		super(bookService, objectMapper, rabbitTemplate, exchange, RabbitConfig.Service.BOOK_SERVICE);
	}

}
