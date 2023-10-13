package com.streamlined.bookshop.service.notification;

import java.time.Instant;
import java.util.List;

import com.streamlined.bookshop.model.BookDto;

public record QueryResultEvent(List<BookDto> bookList, Instant instant, OperationStatus status) {
}
