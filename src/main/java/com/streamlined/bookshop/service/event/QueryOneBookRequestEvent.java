package com.streamlined.bookshop.service.event;

import java.util.List;
import java.util.UUID;

import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;
import java.util.Objects;
import java.util.Optional;

public final class QueryOneBookRequestEvent extends QueryRequestEvent<BookDto, BookService> {

	private UUID bookId;

	public QueryOneBookRequestEvent() {
	}

	public QueryOneBookRequestEvent(UUID bookId) {
		this.bookId = bookId;
	}

	public UUID getBookId() {
		return bookId;
	}

	public void setBookId(UUID bookId) {
		this.bookId = bookId;
	}

	@Override
	public List<BookDto> executeQuery(BookService bookService) {
		Optional<BookDto> dto = bookService.getBook(bookId);
		return dto.map(List::of).orElse(List.of());
	}

	@Override
	public String toString() {
		return "[Query one request: request id=%s, instant=%s, book id=%s]".formatted(Objects.toString(requestId),
				Objects.toString(instant), Objects.toString(bookId));
	}

}
