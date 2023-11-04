package com.streamlined.bookshop.service.event;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public record DeleteRequestEvent(UUID requestId, Instant instant, UUID bookId) implements ModificationRequestEvent {

	@Override
	public Optional<BookDto> executeUpdate(BookService bookService) {
		return bookService.deleteBook(bookId);
	}

	@Override
	public String toString() {
		return "[Delete request: request id=%s, instant=%s, book id=%s]".formatted(Objects.toString(requestId),
				Objects.toString(instant), Objects.toString(bookId));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DeleteRequestEvent e) {
			return Objects.equals(requestId, e.requestId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(requestId);
	}

}
