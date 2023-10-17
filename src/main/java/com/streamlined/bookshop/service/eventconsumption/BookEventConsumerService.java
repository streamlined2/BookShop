package com.streamlined.bookshop.service.eventconsumption;

import org.springframework.amqp.core.Message;

public interface BookEventConsumerService {

	void consumeQueryRequest(Message message);

	void consumeModificationRequest(Message message);

}
