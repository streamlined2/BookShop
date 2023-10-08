package com.streamlined.bookshop.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import com.streamlined.bookshop.model.BookDto;

public interface BookService {

	Stream<BookDto> getAllBooks();

	Optional<BookDto> getBook(UUID id);

	void updateBook(BookDto book, UUID id);

	void deleteBook(UUID id);

	BookDto addBook(BookDto book);
	
}
