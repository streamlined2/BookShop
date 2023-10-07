package com.streamlined.bookshop.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.streamlined.bookshop.dao.BookRepository;
import com.streamlined.bookshop.model.Book;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultBookService implements BookService {

	private final BookRepository bookRepository;

	@Override
	public List<Book> getAllBooks() {
		return bookRepository.getAllBooks();
	}

	@Override
	public Optional<Book> getBook(UUID id) {
		return bookRepository.getBook(id);
	}

	@Override
	public boolean updateBook(Book book, UUID id) {
		return bookRepository.updateBook(book, id);
	}

	@Override
	public boolean deleteBook(UUID id) {
		return bookRepository.deleteBook(id);
	}

	@Override
	public void addBook(Book book) {
		bookRepository.addBook(book);
	}

}
