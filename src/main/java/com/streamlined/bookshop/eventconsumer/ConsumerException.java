package com.streamlined.bookshop.eventconsumer;

public class ConsumerException extends RuntimeException {

	public ConsumerException(String message) {
		super(message);
	}

	public ConsumerException(String message, Throwable cause) {
		super(message, cause);
	}

}
