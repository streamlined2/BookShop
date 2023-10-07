package com.streamlined.bookshop.model;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;

@Builder
public record BookDto(UUID id, String author, String title, String isbn, LocalDate publishDate, Genre genre,
		String country, String language, int pageCount, Size size, Cover cover) {

}
