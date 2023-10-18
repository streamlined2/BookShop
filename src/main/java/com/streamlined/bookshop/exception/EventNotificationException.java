package com.streamlined.bookshop.exception;

public class EventNotificationException extends RuntimeException {

	public EventNotificationException(String message) {
		super(message);
	}

	public EventNotificationException(String message, Throwable cause) {
		super(message, cause);
	}

}
