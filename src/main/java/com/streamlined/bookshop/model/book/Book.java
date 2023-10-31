package com.streamlined.bookshop.model.book;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.Language;

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
	@Field(name = "author", targetType = FieldType.STRING)
	private String author;

	@NonNull
	@Indexed
	@Field(name = "title", targetType = FieldType.STRING)
	private String title;

	@NonNull
	@Indexed(unique = true)
	@Field(name = "isbn", targetType = FieldType.STRING)
	private String isbn;

	@Field(name = "publish_date", targetType = FieldType.DATE_TIME)
	private LocalDate publishDate;

	@Field(name = "genre", targetType = FieldType.OBJECT_ID)
	private Genre genre;

	@Field(name = "country", targetType = FieldType.STRING)
	private String country;

	@Field(name = "language", targetType = FieldType.STRING)
	@Language
	private String language;

	@Field(name = "pageCount", targetType = FieldType.INT32)
	private int pageCount;

	@Field(name = "size")
	private Size size;

	@Field(name = "cover")
	private Cover cover;

	@Override
	public int compareTo(Book book) {
		return Objects.compare(this, book, BY_AUTHOR_TITLE_PUBLISH_DATE_COMPARATOR);
	}

}
