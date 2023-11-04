package com.streamlined.bookshop.service.event.queue;

import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

@Component
public class QueryResultRabbitQueue extends Queue {
	
	public static final String QUEUE_NAME = "queryResult";

	public QueryResultRabbitQueue() {
		super(QUEUE_NAME,false);
	}
	
}
