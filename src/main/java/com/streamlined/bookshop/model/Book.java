package com.streamlined.bookshop.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Book implements Comparable<Book> {

	public enum Genre {
		SCIENTIFICAL, EDUCATIONAL, FICTIONAL, HISTORICAL, BIOGRAPHICAL, PHILOSOPHICAL
	}

	public enum Size {
		FOLIO, QUARTO, OCTAVO, DUODECIMO
	}

	public record Cover(Type type, Surface surface) {
		public enum Type {
			HARD, SOFT
		}

		public enum Surface {
			UNCOATED, SILK, GLOSS
		}

	}

	private static final Comparator<Book> BY_AUTHOR_TITLE_PUBLISH_DATE_COMPARATOR = Comparator
			.comparing(Book::getAuthor).thenComparing(Book::getTitle).thenComparing(Book::getPublishDate);

	@EqualsAndHashCode.Include
	private UUID id;

	private String author;

	private String title;

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
