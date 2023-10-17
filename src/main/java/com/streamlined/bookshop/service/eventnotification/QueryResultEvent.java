package com.streamlined.bookshop.service.eventnotification;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.streamlined.bookshop.model.BookDto;
import com.streamlined.bookshop.service.eventconsumption.QueryRequestEvent;

public record QueryResultEvent(UUID requestId, List<BookDto> bookList, Instant instant, OperationStatus operationStatus)
		implements ResponseEvent {

	@Override
	public UUID getRequestId() {
		return requestId;
	}

	@Override
	public OperationStatus getOperationStatus() {
		return operationStatus;
	}

	@Override
	public Instant getInstant() {
		return instant;
	}

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
		return "[id=%s, book list=%s, instant=%s, status=%s]".formatted(Objects.toString(requestId),
				Objects.toString(bookList), Objects.toString(instant), Objects.toString(operationStatus));
	}

}
