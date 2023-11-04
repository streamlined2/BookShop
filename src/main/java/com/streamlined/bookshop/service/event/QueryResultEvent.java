package com.streamlined.bookshop.service.event;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.streamlined.bookshop.model.book.BookDto;

public record QueryResultEvent(UUID requestId, Instant instant, List<BookDto> bookList, OperationStatus operationStatus)
		implements ResponseEvent {

	@Override
	public boolean equals(Object o) {
		if (o instanceof QueryResultEvent e) {
			return Objects.equals(requestId, e.requestId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(requestId);
	}

	@Override
	public String toString() {
		return "[Query result: request id=%s, book list=%s, instant=%s, status=%s]".formatted(Objects.toString(requestId),
				Objects.toString(bookList), Objects.toString(instant), Objects.toString(operationStatus));
	}

}
