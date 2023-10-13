package com.streamlined.bookshop.service.notification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.streamlined.bookshop.model.BookDto;

public interface BookNotificationService {

	public enum OperationKind {
		UPDATE, DELETE, ADD;
	};

	void publishQueryResult(List<BookDto> bookList, Instant instant, OperationStatus status);

	void publishModificationStatus(OperationKind operation, Optional<BookDto> book, Instant instant,
			OperationStatus status);

}
