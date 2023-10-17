package com.streamlined.bookshop.config.messagebroker.incomingevents;

import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Queue;

@Component
public class ModificationRequestRabbitQueue extends Queue {

	public static final String QUEUE_NAME = "modificationRequest";

	public ModificationRequestRabbitQueue() {
		super(QUEUE_NAME, false);
	}

}
