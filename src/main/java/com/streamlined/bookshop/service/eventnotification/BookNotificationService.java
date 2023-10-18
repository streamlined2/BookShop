package com.streamlined.bookshop.service.eventnotification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.ModifyingOperationKind;

public interface BookNotificationService {

	void publishQueryResult(UUID requestId, List<BookDto> bookList, Instant instant, OperationStatus status);

	void publishModificationStatus(UUID requestId, ModifyingOperationKind operation, Optional<BookDto> book,
			Instant instant, OperationStatus status);

}
