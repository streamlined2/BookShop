package com.streamlined.bookshop;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.streamlined.bookshop.dao.BookRepository;
import com.streamlined.bookshop.dao.InventoryRepository;
import com.streamlined.bookshop.model.book.Book;
import com.streamlined.bookshop.model.book.Cover;
import com.streamlined.bookshop.model.book.Genre;
import com.streamlined.bookshop.model.book.Size;
import com.streamlined.bookshop.model.inventory.Inventory;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableRabbit
@EnableScheduling
@RequiredArgsConstructor
public class BookShopApplication implements CommandLineRunner {

	private final BookRepository bookRepository;
	private final InventoryRepository inventoryRepository;

	public static void main(String[] args) {
		SpringApplication.run(BookShopApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		initializeBooks();
		initializeInventories();
	}

	private void initializeBooks() {
		final List<Book> books = new ArrayList<>(List.of(
				Book.builder().id(UUID.nameUUIDFromBytes("1".getBytes())).author("Jack Peterson")
						.title("Tales of sorcerer").isbn("12345").publishDate(LocalDate.of(2000, 1, 1))
						.genre(Genre.FICTIONAL).country("Britain").language("English").pageCount(100)
						.size(Size.DUODECIMO).cover(new Cover(Cover.Type.HARD, Cover.Surface.GLOSS)).build(),
				Book.builder().id(UUID.nameUUIDFromBytes("2".getBytes())).author("Jane Nickolson").title("Heavens")
						.isbn("56789").publishDate(LocalDate.of(2001, 1, 1)).genre(Genre.BIOGRAPHICAL).country("USA")
						.language("English").pageCount(200).size(Size.FOLIO)
						.cover(new Cover(Cover.Type.SOFT, Cover.Surface.SILK)).build(),
				Book.builder().id(UUID.nameUUIDFromBytes("3".getBytes())).author("Richard Funny")
						.title("Jokes and jests").isbn("90123").publishDate(LocalDate.of(2002, 1, 1))
						.genre(Genre.EDUCATIONAL).country("Australia").language("English").pageCount(50)
						.size(Size.QUARTO).cover(new Cover(Cover.Type.SOFT, Cover.Surface.UNCOATED)).build()));

		bookRepository.deleteAll();

		bookRepository.saveAll(books);
	}

	private void initializeInventories() {
		List<Inventory> inventories = new ArrayList<>(List.of(
				Inventory.builder().id(UUID.nameUUIDFromBytes("1".getBytes()))
						.bookId(UUID.nameUUIDFromBytes("1".getBytes())).amount(BigInteger.valueOf(10))
						.price(BigDecimal.valueOf(100.00)).build(),
				Inventory.builder().id(UUID.nameUUIDFromBytes("2".getBytes()))
						.bookId(UUID.nameUUIDFromBytes("2".getBytes())).amount(BigInteger.valueOf(20))
						.price(BigDecimal.valueOf(200.00)).build(),
				Inventory.builder().id(UUID.nameUUIDFromBytes("3".getBytes()))
						.bookId(UUID.nameUUIDFromBytes("3".getBytes())).amount(BigInteger.valueOf(30))
						.price(BigDecimal.valueOf(300.00)).build()));

		inventoryRepository.deleteAll();

		inventoryRepository.saveAll(inventories);
	}

}
