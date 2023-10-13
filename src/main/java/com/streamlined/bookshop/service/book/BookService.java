package com.streamlined.bookshop.service.book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.streamlined.bookshop.model.BookDto;

public interface BookService {

	List<BookDto> getAllBooks();

	Optional<BookDto> getBook(UUID id);

	void updateBook(BookDto book, UUID id);

	void deleteBook(UUID id);

	BookDto addBook(BookDto book);
	
}
