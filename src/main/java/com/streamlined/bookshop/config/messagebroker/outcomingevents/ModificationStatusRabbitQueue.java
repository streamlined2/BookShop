package com.streamlined.bookshop.config.messagebroker.outcomingevents;

import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

@Component
public class ModificationStatusRabbitQueue extends Queue {

	public static final String QUEUE_NAME = "modificationStatus";

	public ModificationStatusRabbitQueue() {
		super(QUEUE_NAME, false);
	}

}
