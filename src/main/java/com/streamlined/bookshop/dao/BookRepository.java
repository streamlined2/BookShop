package com.streamlined.bookshop.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.streamlined.bookshop.model.Book;

public interface BookRepository {

	List<Book> getAllBooks();

	Optional<Book> getBook(UUID id);

	boolean updateBook(Book book, UUID id);

	boolean deleteBook(UUID id);

	void addBook(Book book);

}
