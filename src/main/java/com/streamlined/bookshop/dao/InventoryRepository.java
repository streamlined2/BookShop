package com.streamlined.bookshop.dao;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.streamlined.bookshop.model.inventory.Inventory;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, UUID> {
}
