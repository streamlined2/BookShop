package com.streamlined.bookshop.service.event;

import java.util.List;
import java.util.Objects;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public final class QueryAllBookRequestEvent extends QueryRequestEvent<BookDto, BookService> {
	
	@Override
	public List<BookDto> executeQuery(BookService bookService) {
		return bookService.getAllBooks();
	}

	@Override
	public String toString() {
		return "[Query all request: request id=%s, instant=%s]".formatted(Objects.toString(requestId),
				Objects.toString(instant));
	}

}
