package com.streamlined.bookshop.exception;

public class BookAlreadyAddedException extends RuntimeException  {

	public BookAlreadyAddedException(String message) {
		super(message);
	}

	public BookAlreadyAddedException(String message, Throwable cause) {
		super(message, cause);
	}

}
