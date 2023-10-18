package com.streamlined.bookshop.exception;

public class NoInventoryFoundException extends RuntimeException {

	public NoInventoryFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoInventoryFoundException(String message) {
		super(message);
	}

}
