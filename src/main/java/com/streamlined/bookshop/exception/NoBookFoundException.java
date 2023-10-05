package com.streamlined.bookshop.exception;

public class NoBookFoundException extends RuntimeException {

	public NoBookFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoBookFoundException(String message) {
		super(message);
	}

}
