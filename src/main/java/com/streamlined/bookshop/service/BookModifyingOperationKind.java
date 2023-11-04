package com.streamlined.bookshop.service;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public enum BookModifyingOperationKind {
	
	UPDATE {

		@Override
		public Optional<BookDto> executeUpdate(BookService bookService, ObjectMapper objectMapper, Object... params) {
			BookDto book = objectMapper.convertValue((params[0]), BookDto.class);
			UUID id = UUID.fromString((String) params[1]);
			return bookService.updateBook(book, id);
		}

	},
	DELETE {

		@Override
		public Optional<BookDto> executeUpdate(BookService bookService, ObjectMapper objectMapper, Object... params) {
			UUID id = UUID.fromString((String) params[0]);
			return bookService.deleteBook(id);
		}

	},
	ADD {

		@Override
		public Optional<BookDto> executeUpdate(BookService bookService, ObjectMapper objectMapper, Object... params) {
			BookDto book = objectMapper.convertValue((params[0]), BookDto.class);
			return bookService.addBook(book);
		}

	};

	public abstract Optional<BookDto> executeUpdate(BookService bookService, ObjectMapper objectMapper, Object... params);
}
