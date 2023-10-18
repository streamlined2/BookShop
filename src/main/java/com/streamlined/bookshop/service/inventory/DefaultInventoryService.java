package com.streamlined.bookshop.service.inventory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.streamlined.bookshop.dao.InventoryRepository;
import com.streamlined.bookshop.model.inventory.Inventory;
import com.streamlined.bookshop.model.inventory.InventoryDto;
import com.streamlined.bookshop.model.inventory.InventoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultInventoryService implements InventoryService {

	private final InventoryRepository inventoryRepository;
	private final InventoryMapper inventoryMapper;

	@Override
	public List<InventoryDto> getAllInventories() {
		return inventoryRepository.findAll().stream().map(inventoryMapper::toDto).toList();
	}

	@Override
	public Optional<InventoryDto> getInventory(UUID id) {
		return inventoryRepository.findById(id).map(inventoryMapper::toDto);
	}

	@Override
	@Transactional
	public Optional<InventoryDto> updateInventory(InventoryDto inventory, UUID id) {
		Inventory entity = inventoryMapper.toEntity(inventory);
		entity.setId(id);
		return Optional.ofNullable(inventoryRepository.save(entity)).map(inventoryMapper::toDto);
	}

	@Override
	@Transactional
	public Optional<InventoryDto> deleteInventory(UUID id) {
		Optional<Inventory> inventory = inventoryRepository.findById(id);
		inventory.map(Inventory::getId).ifPresent(inventoryRepository::deleteById);
		return inventory.map(inventoryMapper::toDto);
	}

	@Override
	@Transactional
	public Optional<InventoryDto> addInventory(InventoryDto inventory) {
		Inventory entity = inventoryMapper.toEntity(inventory);
		return Optional.ofNullable(inventoryRepository.insert(entity)).map(inventoryMapper::toDto);
	}

	@Override
	@Transactional
	public Optional<InventoryDto> replenishInventory(UUID id, BigInteger amount) {
		Optional<Inventory> entity = inventoryRepository.findById(id);
		return entity.map(inv -> updateEntity(inv, Inventory::addAmount, amount)).map(inventoryMapper::toDto);
	}

	private <T> Inventory updateEntity(Inventory inventory, BiConsumer<Inventory, T> updater, T argument) {
		updater.accept(inventory, argument);
		return inventoryRepository.save(inventory);
	}

	@Override
	@Transactional
	public Optional<InventoryDto> sellInventory(UUID id, BigInteger amount) {
		Optional<Inventory> entity = inventoryRepository.findById(id);
		return entity.map(inv -> updateEntity(inv, Inventory::subtractAmount, amount)).map(inventoryMapper::toDto);
	}

	@Override
	@Transactional
	public Optional<InventoryDto> assignPrice(UUID id, BigDecimal price) {
		Optional<Inventory> entity = inventoryRepository.findById(id);
		return entity.map(inv -> updateEntity(inv, Inventory::setPrice, price)).map(inventoryMapper::toDto);
	}

}
