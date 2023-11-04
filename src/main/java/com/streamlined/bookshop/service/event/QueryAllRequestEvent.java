package com.streamlined.bookshop.service.event;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

public record QueryAllRequestEvent(UUID requestId, Instant instant) implements QueryRequestEvent {

	@Override
	public List<BookDto> executeQuery(BookService bookService) {
		return bookService.getAllBooks();
	}

	@Override
	public String toString() {
		return "[Query All request: request id=%s, instant=%s]".formatted(Objects.toString(requestId),
				Objects.toString(instant));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof QueryAllRequestEvent e) {
			return Objects.equals(requestId, e.requestId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(requestId);
	}

}
