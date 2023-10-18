package com.streamlined.bookshop.model.inventory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
	private BigInteger amount;

	@NonNull
	private BigDecimal price;

	public void addAmount(BigInteger extraAmount) {
		amount = amount.add(extraAmount);
	}

	public void subtractAmount(BigInteger extraAmount) {
		amount = amount.subtract(extraAmount);
	}

}
