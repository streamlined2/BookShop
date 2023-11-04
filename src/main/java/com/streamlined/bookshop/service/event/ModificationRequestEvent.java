package com.streamlined.bookshop.service.event;

import java.util.Optional;

import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public interface ModificationRequestEvent extends RequestEvent {

	public Optional<BookDto> executeUpdate(BookService bookService);

}
