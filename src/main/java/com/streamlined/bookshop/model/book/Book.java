package com.streamlined.bookshop.model.book;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Document(collection = "books")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Book implements Comparable<Book> {

	private static final Comparator<Book> BY_AUTHOR_TITLE_PUBLISH_DATE_COMPARATOR = Comparator
			.comparing(Book::getAuthor).thenComparing(Book::getTitle).thenComparing(Book::getPublishDate);

	@Id
	@NonNull
	@EqualsAndHashCode.Include
	private UUID id;

	@NonNull
	@Indexed
	private String author;

	@NonNull
	@Indexed
	private String title;

	@NonNull
	@Indexed(unique = true)
	private String isbn;

	private LocalDate publishDate;

	private Genre genre;

	private String country;

	private String language;

	private int pageCount;

	private Size size;

	private Cover cover;

	@Override
	public int compareTo(Book book) {
		return Objects.compare(this, book, BY_AUTHOR_TITLE_PUBLISH_DATE_COMPARATOR);
	}

}
