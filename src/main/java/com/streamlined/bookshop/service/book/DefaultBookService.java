package com.streamlined.bookshop.service.book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamlined.bookshop.dao.BookRepository;
import com.streamlined.bookshop.model.Book;
import com.streamlined.bookshop.model.BookDto;
import com.streamlined.bookshop.model.BookMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultBookService implements BookService {

	private final BookRepository bookRepository;
	private final BookMapper bookMapper;

	@Override
	public List<BookDto> getAllBooks() {
		return bookRepository.findAll().stream().map(bookMapper::toDto).toList();
	}

	@Override
	public Optional<BookDto> getBook(UUID id) {
		return bookRepository.findById(id).map(bookMapper::toDto);
	}

	@Override
	@Transactional
	public Optional<BookDto> updateBook(BookDto book, UUID id) {
		Book entity = bookMapper.toEntity(book);
		entity.setId(id);
		return Optional.ofNullable(bookRepository.save(entity)).map(bookMapper::toDto);
	}

	@Override
	@Transactional
	public Optional<BookDto> deleteBook(UUID id) {
		Optional<Book> book = bookRepository.findById(id);
		book.map(Book::getId).ifPresent(bookRepository::deleteById);
		return book.map(bookMapper::toDto);
	}

	@Override
	@Transactional
	public Optional<BookDto> addBook(BookDto book) {
		Book entity = bookMapper.toEntity(book);
		return Optional.ofNullable(bookRepository.insert(entity)).map(bookMapper::toDto);
	}

}
