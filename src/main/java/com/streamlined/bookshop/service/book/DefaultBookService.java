package com.streamlined.bookshop.service.book;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamlined.bookshop.dao.BookRepository;
import com.streamlined.bookshop.model.BookDto;
import com.streamlined.bookshop.model.BookMapper;
import com.streamlined.bookshop.service.notification.BookNotificationService;
import com.streamlined.bookshop.service.notification.BookNotificationService.OperationKind;
import com.streamlined.bookshop.service.notification.OperationStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultBookService implements BookService {

	private final BookRepository bookRepository;
	private final BookMapper bookMapper;
	private final List<BookNotificationService> notificationServiceList;

	@Override
	public List<BookDto> getAllBooks() {
		List<BookDto> bookList = bookRepository.findAll().stream().map(bookMapper::toDto).toList();
		publishQueryResultMessage(bookList);
		return bookList;
	}

	private void publishQueryResultMessage(List<BookDto> bookList) {
		notificationServiceList
				.forEach(service -> service.publishQueryResult(bookList, Instant.now(), OperationStatus.SUCCESS));
	}

	@Override
	public Optional<BookDto> getBook(UUID id) {
		Optional<BookDto> book = bookRepository.findById(id).map(bookMapper::toDto);
		book.map(List::of).ifPresent(this::publishQueryResultMessage);
		return book;
	}

	@Override
	@Transactional
	public void updateBook(BookDto book, UUID id) {
		var entity = bookMapper.toEntity(book);
		entity.setId(id);
		bookRepository.save(entity);
		publishModificationStatusMessage(OperationKind.UPDATE, Optional.of(bookMapper.toDto(entity)), Instant.now(),
				OperationStatus.SUCCESS);
	}

	private void publishModificationStatusMessage(OperationKind operation, Optional<BookDto> book, Instant instant,
			OperationStatus status) {
		notificationServiceList.forEach(service -> service.publishModificationStatus(operation, book, instant, status));
	}

	@Override
	@Transactional
	public void deleteBook(UUID id) {
		var book = bookRepository.findById(id).map(bookMapper::toDto);
		bookRepository.deleteById(id);
		publishModificationStatusMessage(OperationKind.DELETE, book, Instant.now(), OperationStatus.SUCCESS);
	}

	@Override
	@Transactional
	public BookDto addBook(BookDto book) {
		var newBook = bookMapper.toDto(bookRepository.insert(bookMapper.toEntity(book)));
		publishModificationStatusMessage(OperationKind.ADD, Optional.of(newBook), Instant.now(),
				OperationStatus.SUCCESS);
		return newBook;
	}

}
