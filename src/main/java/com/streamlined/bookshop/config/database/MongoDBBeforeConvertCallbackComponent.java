package com.streamlined.bookshop.config.database;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import com.streamlined.bookshop.model.book.Book;

@Component
public class MongoDBBeforeConvertCallbackComponent implements BeforeConvertCallback<Book> {

	@Override
	public Book onBeforeConvert(Book entity, String collection) {
		if (entity.getId() == null) {
			entity.setId(UUID.randomUUID());
		}
		return entity;
	}

}
