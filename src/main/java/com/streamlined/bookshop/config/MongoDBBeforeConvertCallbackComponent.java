package com.streamlined.bookshop.config;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import com.streamlined.bookshop.model.Book;

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
