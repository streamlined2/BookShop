package com.streamlined.bookshop.model.inventory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import lombok.Builder;
import lombok.NonNull;
import lombok.With;

@Builder
public record InventoryDto(@NonNull UUID id, @NonNull UUID bookId, @With @NonNull BigInteger amount,
		@With @NonNull BigDecimal price) {
}
