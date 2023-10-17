package com.streamlined.bookshop.config.messagebroker.incomingevents;

import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

@Component
public class QueryRequestRabbitQueue extends Queue {

	public static final String QUEUE_NAME = "queryRequest";

	public QueryRequestRabbitQueue() {
		super(QUEUE_NAME, false);
	}

}
