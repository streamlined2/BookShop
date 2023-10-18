package com.streamlined.bookshop.model.book;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record BookDto(@NonNull UUID id, @NonNull String author, @NonNull String title, @NonNull String isbn,
		LocalDate publishDate, Genre genre, String country, String language, int pageCount, Size size, Cover cover) {
}
