package com.streamlined.bookshop.service.event;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public final class DeleteBookRequestEvent extends ModificationRequestEvent<BookDto, BookService> {

	private UUID bookId;

	public DeleteBookRequestEvent() {
	}

	public DeleteBookRequestEvent(UUID bookId) {
		this.bookId = bookId;
	}

	public UUID getBookId() {
		return bookId;
	}

	public void setBookId(UUID bookId) {
		this.bookId = bookId;
	}

	@Override
	public Optional<BookDto> executeUpdate(BookService bookService) {
		return bookService.deleteBook(bookId);
	}

	@Override
	public String toString() {
		return "[Delete request: request id=%s, instant=%s, book id=%s]".formatted(Objects.toString(requestId),
				Objects.toString(instant), Objects.toString(bookId));
	}

}
