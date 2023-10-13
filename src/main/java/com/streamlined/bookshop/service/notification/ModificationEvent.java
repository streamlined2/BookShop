package com.streamlined.bookshop.service.notification;

import java.time.Instant;
import com.streamlined.bookshop.model.BookDto;
import com.streamlined.bookshop.service.notification.BookNotificationService.OperationKind;

public record ModificationEvent(OperationKind operation, BookDto book, Instant instant, OperationStatus status) {
}
