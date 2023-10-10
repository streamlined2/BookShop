package com.streamlined.bookshop.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
	private final List<NotificationService> notificationServiceList;

	@Override
	public List<BookDto> getAllBooks() {
		List<BookDto> bookList = bookRepository.findAll().stream().map(bookMapper::toDto).toList();
		publishQueryResultMessage(bookList);
		return bookList;
	}

	private void publishQueryResultMessage(final Object message) {
		notificationServiceList.forEach(service -> service.publishQueryResult(message));
	}

	@Override
	public Optional<BookDto> getBook(UUID id) {
		Optional<BookDto> book = bookRepository.findById(id).map(bookMapper::toDto);
		book.map(List::of).ifPresent(this::publishQueryResultMessage);
		return book;
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
