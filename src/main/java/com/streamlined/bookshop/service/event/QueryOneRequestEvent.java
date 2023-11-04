package com.streamlined.bookshop.service.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;
import java.util.Objects;
import java.util.Optional;

public record QueryOneRequestEvent(UUID requestId, Instant instant, UUID bookId) implements QueryRequestEvent {

	@Override
	public List<BookDto> executeQuery(BookService bookService) {
		Optional<BookDto> dto = bookService.getBook(bookId);
		return dto.map(List::of).orElse(List.of());
	}

	@Override
	public String toString() {
		return "[Query One request: request id=%s, instant=%s, book id=%s]".formatted(Objects.toString(requestId),
				Objects.toString(instant), Objects.toString(bookId));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof QueryOneRequestEvent e) {
			return Objects.equals(requestId, e.requestId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(requestId);
	}

}
