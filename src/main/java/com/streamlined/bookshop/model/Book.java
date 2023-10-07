package com.streamlined.bookshop.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Book implements Comparable<Book> {

	private static final Comparator<Book> BY_AUTHOR_TITLE_PUBLISH_DATE_COMPARATOR = Comparator
			.comparing(Book::getAuthor).thenComparing(Book::getTitle).thenComparing(Book::getPublishDate);

	@Id
	@UuidGenerator
	@org.hibernate.validator.constraints.UUID
	@EqualsAndHashCode.Include
	private UUID id;

	@Indexed(background = true)
	private String author;

	@Indexed(background = true)
	private String title;

	@Indexed(background = true, unique = true)
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
