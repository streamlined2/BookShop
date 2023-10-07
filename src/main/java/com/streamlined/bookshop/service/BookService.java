package com.streamlined.bookshop.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import com.streamlined.bookshop.model.BookDto;

public interface BookService {

	Stream<BookDto> getAllBooks();

	Optional<BookDto> getBook(UUID id);

	boolean updateBook(BookDto book, UUID id);

	boolean deleteBook(UUID id);

	void addBook(BookDto book);
	
}
