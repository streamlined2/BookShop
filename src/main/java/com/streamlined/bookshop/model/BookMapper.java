package com.streamlined.bookshop.model;

import org.springframework.stereotype.Component;

@Component
public class BookMapper {

	public Book toEntity(BookDto dto) {
		return Book.builder().id(dto.id()).author(dto.author()).title(dto.title()).isbn(dto.isbn())
				.publishDate(dto.publishDate()).genre(dto.genre()).country(dto.country()).language(dto.language())
				.pageCount(dto.pageCount()).size(dto.size()).cover(dto.cover()).build();
	}

	public BookDto toDto(Book entity) {
		return BookDto.builder().id(entity.getId()).author(entity.getAuthor()).title(entity.getTitle())
				.isbn(entity.getIsbn()).publishDate(entity.getPublishDate()).genre(entity.getGenre())
				.country(entity.getCountry()).language(entity.getLanguage()).pageCount(entity.getPageCount())
				.size(entity.getSize()).cover(entity.getCover()).build();
	}

}
