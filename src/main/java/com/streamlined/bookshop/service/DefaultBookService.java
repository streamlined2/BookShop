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
		return bookRepository.findAll().stream().map(bookMapper::toDto);
	}

	@Override
	public Optional<BookDto> getBook(UUID id) {
		return bookRepository.findById(id).map(bookMapper::toDto);
	}

	@Override
	public void updateBook(BookDto book, UUID id) {
		var entity = bookMapper.toEntity(book);
		entity.setId(id);
		bookRepository.save(entity);
	}

	@Override
	public void deleteBook(UUID id) {
		bookRepository.deleteById(id);
	}

	@Override
	public BookDto addBook(BookDto book) {
		return bookMapper.toDto(bookRepository.insert(bookMapper.toEntity(book)));
	}

}
