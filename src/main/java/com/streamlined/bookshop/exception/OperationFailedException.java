package com.streamlined.bookshop.exception;

public class OperationFailedException extends RuntimeException {

	public OperationFailedException(String message) {
		super(message);
	}

	public OperationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
