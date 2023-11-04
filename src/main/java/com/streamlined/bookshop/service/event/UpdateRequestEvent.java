package com.streamlined.bookshop.service.event;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public record UpdateRequestEvent(UUID requestId, Instant instant, BookDto book, UUID bookId)
		implements ModificationRequestEvent {

	@Override
	public Optional<BookDto> executeUpdate(BookService bookService) {
		return bookService.updateBook(book, bookId);
	}

	@Override
	public String toString() {
		return "[Update request: request id=%s, instant=%s, book=%s, book id=%s]".formatted(Objects.toString(requestId),
				Objects.toString(instant), Objects.toString(book), Objects.toString(bookId));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof UpdateRequestEvent e) {
			return Objects.equals(requestId, e.requestId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(requestId);
	}

}
