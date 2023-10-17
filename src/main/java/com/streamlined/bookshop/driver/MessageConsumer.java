package com.streamlined.bookshop.driver;

import org.springframework.amqp.core.Message;

public interface MessageConsumer {

	void consumeQueryResultMessage(Message message);

	void consumeModificationStatusMessage(Message message);

}
