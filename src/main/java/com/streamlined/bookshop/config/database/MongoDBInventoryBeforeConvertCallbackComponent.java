package com.streamlined.bookshop.config.database;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import com.streamlined.bookshop.model.inventory.Inventory;

@Component
public class MongoDBInventoryBeforeConvertCallbackComponent implements BeforeConvertCallback<Inventory> {

	@Override
	public Inventory onBeforeConvert(Inventory entity, String collection) {
		if (entity.getId() == null) {
			entity.setId(UUID.randomUUID());
		}
		return entity;
	}

}
