package com.streamlined.bookshop.service.event;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public record AddRequestEvent(UUID requestId, Instant instant, BookDto book) implements ModificationRequestEvent {

	@Override
	public Optional<BookDto> executeUpdate(BookService bookService) {
		return bookService.addBook(book);
	}

	@Override
	public String toString() {
		return "[Add request: request id=%s, instant=%s, book=%s]".formatted(Objects.toString(requestId),
				Objects.toString(instant), Objects.toString(book));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AddRequestEvent e) {
			return Objects.equals(requestId, e.requestId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(requestId);
	}

}
