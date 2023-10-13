package com.streamlined.bookshop.eventconsumer;

public interface MessageConsumer {

	void consumeQueryResultMessage(Object message);

	void consumeModificationStatusMessage(Object message);

}
