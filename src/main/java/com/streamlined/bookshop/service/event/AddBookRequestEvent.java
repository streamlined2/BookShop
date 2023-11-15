package com.streamlined.bookshop.service.event;

import java.util.Objects;
import java.util.Optional;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public final class AddBookRequestEvent extends ModificationRequestEvent<BookDto, BookService> {

	private BookDto book;
	
	public AddBookRequestEvent() {
	}

	public AddBookRequestEvent(BookDto book) {
		this.book = book;
	}

	public BookDto getBook() {
		return book;
	}

	public void setBook(BookDto book) {
		this.book = book;
	}

	@Override
	public Optional<BookDto> executeUpdate(BookService service) {
		return service.addBook(book);
	}

	@Override
	public String toString() {
		return "[Add request: request id=%s, instant=%s, book=%s]".formatted(Objects.toString(requestId),
				Objects.toString(instant), Objects.toString(book));
	}

}
