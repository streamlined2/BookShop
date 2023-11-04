package com.streamlined.bookshop.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public enum BookQueryingOperationKind {
	
	QUERY_ALL {
		
		@Override
		public List<BookDto> executeQuery(BookService bookService, Object... params) {
			return bookService.getAllBooks();
		}
	},
	QUERY_ONE_BY_ID {

		@Override
		public List<BookDto> executeQuery(BookService bookService, Object... params) {
			UUID id = UUID.fromString((String) params[0]);
			Optional<BookDto> dto = bookService.getBook(id);
			return dto.map(List::of).orElse(List.of());
		}
	};

	public abstract List<BookDto> executeQuery(BookService bookService, Object... params);
}
