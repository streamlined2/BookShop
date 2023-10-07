package com.streamlined.bookshop.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.streamlined.bookshop.dao.BookRepository;
import com.streamlined.bookshop.model.BookDto;
import com.streamlined.bookshop.model.BookMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultBookService implements BookService {

	private final BookRepository bookRepository;
	private final BookMapper bookMapper;

	@Override
	public Stream<BookDto> getAllBooks() {
		return bookRepository.getAllBooks().stream().map(bookMapper::toDto);
	}

	@Override
	public Optional<BookDto> getBook(UUID id) {
		return bookRepository.getBook(id).map(bookMapper::toDto);
	}

	@Override
	public boolean updateBook(BookDto book, UUID id) {
		return bookRepository.updateBook(bookMapper.toEntity(book), id);
	}

	@Override
	public boolean deleteBook(UUID id) {
		return bookRepository.deleteBook(id);
	}

	@Override
	public void addBook(BookDto book) {
		bookRepository.addBook(bookMapper.toEntity(book));
	}

}
