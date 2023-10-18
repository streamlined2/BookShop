package com.streamlined.bookshop.service.book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.streamlined.bookshop.model.book.BookDto;

public interface BookService {

	List<BookDto> getAllBooks();

	Optional<BookDto> getBook(UUID id);

	Optional<BookDto> updateBook(BookDto book, UUID id);

	Optional<BookDto> deleteBook(UUID id);

	Optional<BookDto> addBook(BookDto book);

}
