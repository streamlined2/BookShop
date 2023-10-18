package com.streamlined.bookshop.service.inventory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.streamlined.bookshop.model.inventory.InventoryDto;

public interface InventoryService {

	List<InventoryDto> getAllInventories();

	Optional<InventoryDto> getInventory(UUID id);

	Optional<InventoryDto> updateInventory(InventoryDto book, UUID id);

	Optional<InventoryDto> deleteInventory(UUID id);

	Optional<InventoryDto> addInventory(InventoryDto book);

	Optional<InventoryDto> replenishInventory(UUID id, BigInteger amount);

	Optional<InventoryDto> sellInventory(UUID id, BigInteger amount);

	Optional<InventoryDto> assignPrice(UUID id, BigDecimal price);

}
