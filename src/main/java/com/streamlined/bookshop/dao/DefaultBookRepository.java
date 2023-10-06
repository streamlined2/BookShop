package com.streamlined.bookshop.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.streamlined.bookshop.exception.BookAlreadyAddedException;
import com.streamlined.bookshop.model.Book;
import com.streamlined.bookshop.model.Book.Cover;
import com.streamlined.bookshop.model.Book.Genre;
import com.streamlined.bookshop.model.Book.Size;

@Repository
public class DefaultBookRepository implements BookRepository {

	private final List<Book> books = new ArrayList<>(List.of(
			Book.builder().id(UUID.nameUUIDFromBytes("1".getBytes())).author("Jack Peterson").title("Tales of sorcerer")
					.isbn("12345").publishDate(LocalDate.of(2000, 1, 1)).genre(Genre.FICTIONAL).country("Britain")
					.language("English").pageCount(100).size(Size.DUODECIMO)
					.cover(new Cover(Cover.Type.HARD, Cover.Surface.GLOSS)).build(),
			Book.builder().id(UUID.nameUUIDFromBytes("2".getBytes())).author("Jane Nickolson").title("Heavens")
					.isbn("56789").publishDate(LocalDate.of(2001, 1, 1)).genre(Genre.BIOGRAPHICAL).country("USA")
					.language("English").pageCount(200).size(Size.FOLIO)
					.cover(new Cover(Cover.Type.SOFT, Cover.Surface.SILK)).build(),
			Book.builder().id(UUID.nameUUIDFromBytes("3".getBytes())).author("Richard Funny").title("Jokes and jests")
					.isbn("90123").publishDate(LocalDate.of(2002, 1, 1)).genre(Genre.EDUCATIONAL).country("Australia")
					.language("English").pageCount(50).size(Size.QUARTO)
					.cover(new Cover(Cover.Type.SOFT, Cover.Surface.UNCOATED)).build()));

	@Override
	public List<Book> getAllBooks() {
		return books;
	}

	@Override
	public Optional<Book> getBook(UUID id) {
		return books.stream().filter(book -> book.getId().equals(id)).findFirst();
	}

	@Override
	public boolean updateBook(Book book, UUID id) {
		for (var i = books.listIterator(); i.hasNext();) {
			var b = i.next();
			if (b.getId().equals(id)) {
				i.set(book);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean deleteBook(UUID id) {
		for (var i = books.listIterator(); i.hasNext();) {
			var b = i.next();
			if (b.getId().equals(id)) {
				i.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public void addBook(Book book) {
		if (getBook(book.getId()).isPresent()) {
			throw new BookAlreadyAddedException("book already added to list");
		}
		books.add(book);
	}

}
