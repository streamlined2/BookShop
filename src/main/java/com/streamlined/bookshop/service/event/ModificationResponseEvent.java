package com.streamlined.bookshop.service.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.streamlined.bookshop.model.book.BookDto;

public record ModificationResponseEvent(UUID requestId, BookDto book, Instant instant, OperationStatus operationStatus)
		implements ResponseEvent {

	@Override
	public boolean equals(Object o) {
		if (o instanceof ModificationResponseEvent e) {
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
		return "[Modification response: request id=%s, book=%s, instant=%s, status=%s]".formatted(
				Objects.toString(requestId), Objects.toString(book), Objects.toString(instant),
				Objects.toString(operationStatus));
	}

}
