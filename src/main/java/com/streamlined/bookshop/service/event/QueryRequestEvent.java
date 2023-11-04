package com.streamlined.bookshop.service.event;

import java.util.List;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public interface QueryRequestEvent extends RequestEvent {

	public List<BookDto> executeQuery(BookService bookService);

}
