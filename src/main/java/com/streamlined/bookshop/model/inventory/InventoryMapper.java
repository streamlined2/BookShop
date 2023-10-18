package com.streamlined.bookshop.model.inventory;

import org.springframework.stereotype.Component;

import com.streamlined.bookshop.model.Mapper;

@Component
public class InventoryMapper implements Mapper<Inventory, InventoryDto> {

	@Override
	public Inventory toEntity(InventoryDto dto) {
		return Inventory.builder().id(dto.id()).bookId(dto.bookId()).amount(dto.amount()).price(dto.price()).build();
	}

	@Override
	public InventoryDto toDto(Inventory entity) {
		return InventoryDto.builder().id(entity.getId()).bookId(entity.getBookId()).amount(entity.getAmount())
				.price(entity.getPrice()).build();
	}

}
