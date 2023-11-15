package com.streamlined.bookshop.service.event;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public final class UpdateBookRequestEvent extends ModificationRequestEvent<BookDto, BookService> {

	private BookDto book;
	private UUID bookId;

	public UpdateBookRequestEvent() {
	}

	public UpdateBookRequestEvent(BookDto book, UUID bookId) {
		this.book = book;
		this.bookId = bookId;
	}

	public BookDto getBook() {
		return book;
	}

	public void setBook(BookDto book) {
		this.book = book;
	}

	public UUID getBookId() {
		return bookId;
	}

	public void setBookId(UUID bookId) {
		this.bookId = bookId;
	}

	@Override
	public Optional<BookDto> executeUpdate(BookService bookService) {
		return bookService.updateBook(book, bookId);
	}

	@Override
	public String toString() {
		return "[Update request: request id=%s, instant=%s, book=%s, book id=%s]".formatted(Objects.toString(requestId),
				Objects.toString(instant), Objects.toString(book), Objects.toString(bookId));
	}

}
