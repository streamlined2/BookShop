package com.streamlined.bookshop.model.inventory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Document(collection = "inventories")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Inventory {

	@Id
	@EqualsAndHashCode.Include
	private UUID id;

	@Indexed
	private UUID bookId;

	@NonNull
	@Field(name = "amount", targetType = FieldType.INT64)
	private BigInteger amount;

	@NonNull
	@Field(name = "price", targetType = FieldType.DECIMAL128)
	private BigDecimal price;

	public void addAmount(BigInteger extraAmount) {
		amount = amount.add(extraAmount);
	}

	public void subtractAmount(BigInteger extraAmount) {
		amount = amount.subtract(extraAmount);
	}

}
