package com.streamlined.bookshop.service.eventconsumption;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.streamlined.bookshop.service.BookModifyingOperationKind;

public record ModificationRequestEvent(UUID requestId, BookModifyingOperationKind kind, Object... params) {

	@Override
	public boolean equals(Object o) {
		if (o instanceof QueryRequestEvent e) {
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
		return "[id=%s, kind=%s, params=%s]".formatted(Objects.toString(requestId), Objects.toString(kind),
				Stream.of(params).map(Objects::toString).collect(Collectors.joining(",", "{", "}")));
	}

}
