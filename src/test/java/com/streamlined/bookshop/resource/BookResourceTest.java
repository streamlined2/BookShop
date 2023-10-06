package com.streamlined.bookshop.resource;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.streamlined.bookshop.dao.BookRepository;
import com.streamlined.bookshop.dao.DefaultBookRepository;
import com.streamlined.bookshop.exception.BookAlreadyAddedMapper;
import com.streamlined.bookshop.exception.NoBookFoundMapper;
import com.streamlined.bookshop.model.Book;
import com.streamlined.bookshop.model.Book.Cover;
import com.streamlined.bookshop.model.Book.Genre;
import com.streamlined.bookshop.model.Book.Size;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

class BookResourceTest extends JerseyTest {

	private AnnotationConfigApplicationContext appContext;

	@Override
	protected Application configure() {
		appContext = new AnnotationConfigApplicationContext(DefaultBookRepository.class);
		final ResourceConfig config = new ResourceConfig(BookResource.class, BookAlreadyAddedMapper.class,
				NoBookFoundMapper.class);
		config.property("contextConfig", appContext);
		return config;
	}

	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
	}

	@AfterEach
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	@DisplayName("return book list for correct GET request")
	void givenGetAllBooks_whenCorrectRequest_thenResponseShouldBeOkAndReturnListOfBooks() {
		final String resourceLocation = "/books";
		Response response = target(resourceLocation).request().get();

		Book[] expectedContent = new Book[] {
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
						.size(Size.QUARTO).cover(new Cover(Cover.Type.SOFT, Cover.Surface.UNCOATED)).build() };
		Book[] content = response.readEntity(Book[].class);

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		assertNull(response.getLocation());
		assertArrayEquals(expectedContent, content);
	}

	@Test
	@DisplayName("report not found if request URI is wrong")
	void givenGetAllBooks_whenWrongRequestURI_thenResponseShouldBeNotFound() {
		final String resourceLocation = "/albums";
		Response response = target(resourceLocation).request().get();

		assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

	@Test
	@DisplayName("return found book for correct GET request")
	void givenGetBook_whenCorrectRequestAndBookFound_thenResponseShouldBeOkAndReturnBook() {
		final String resourceLocation = "/books/c4ca4238-a0b9-3382-8dcc-509a6f75849b";
		Response response = target(resourceLocation).request().get();

		Book expectedBook = Book.builder().id(UUID.nameUUIDFromBytes("1".getBytes())).author("Jack Peterson")
				.title("Tales of sorcerer").isbn("12345").publishDate(LocalDate.of(2000, 1, 1)).genre(Genre.FICTIONAL)
				.country("Britain").language("English").pageCount(100).size(Size.DUODECIMO)
				.cover(new Cover(Cover.Type.HARD, Cover.Surface.GLOSS)).build();
		Book actualBook = response.readEntity(Book.class);

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		assertNull(response.getLocation());
		assertEquals(expectedBook, actualBook);
	}

	@Test
	@DisplayName("report book not found if id is wrong")
	void givenGetBook_whenCorrectRequestAndBookNotFound_thenResponseShouldBeNotFound() {
		final String resourceLocation = "/books/c4ca4238-a0b9-3382-8dcc-509a6f75849c";
		Response response = target(resourceLocation).request().get();

		assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

	@Test
	@DisplayName("report OK status for correct PUT request and update book")
	void givenUpdateBook_whenCorrectRequestAndBookFound_thenResponseShouldBeOkAndBookUpdated() {
		final String uuid = "c4ca4238-a0b9-3382-8dcc-509a6f75849b";
		final String resourceLocation = "/books/" + uuid;

		Book expectedBook = Book.builder().id(UUID.fromString(uuid)).author("Jennifer Peterson").title("Tales of witch")
				.isbn("23456").publishDate(LocalDate.of(2000, 1, 1)).genre(Genre.FICTIONAL).country("Britain")
				.language("English").pageCount(100).size(Size.DUODECIMO)
				.cover(new Cover(Cover.Type.HARD, Cover.Surface.GLOSS)).build();
		Response response = target(resourceLocation).request()
				.put(Entity.entity(expectedBook, MediaType.APPLICATION_JSON));

		Book actualBook = appContext.getBean(BookRepository.class).getBook(UUID.fromString(uuid)).get();

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
		assertEquals(expectedBook, actualBook);
	}

	@Test
	@DisplayName("report Not Modified status if incorrect PUT request and book should remain unchanged")
	void givenUpdateBook_whenIncorrectRequestOrBookNotFound_thenResponseShouldBeNotModifiedAndBookRemainsUnchanged() {
		final String uuid = "c4ca4238-a0b9-3382-8dcc-509a6f75849b";
		final String wrongUuid = "c4ca4238-a0b9-3382-8dcc-509a6f75849a";
		final String resourceLocation = "/books/" + wrongUuid;

		Book originalBook = appContext.getBean(BookRepository.class).getBook(UUID.fromString(uuid)).get();

		Book expectedBook = Book.builder().id(UUID.fromString(uuid)).author("Jennifer Peterson").title("Tales of witch")
				.isbn("23456").publishDate(LocalDate.of(2000, 1, 1)).genre(Genre.FICTIONAL).country("Britain")
				.language("English").pageCount(100).size(Size.DUODECIMO)
				.cover(new Cover(Cover.Type.HARD, Cover.Surface.GLOSS)).build();
		Response response = target(resourceLocation).request()
				.put(Entity.entity(expectedBook, MediaType.APPLICATION_JSON));

		Book actualBook = appContext.getBean(BookRepository.class).getBook(UUID.fromString(uuid)).get();

		assertEquals(Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
		assertEquals(originalBook, actualBook);
	}

	@Test
	@DisplayName("report OK status for correct DELETE request and book should be deleted")
	void givenDeleteBook_whenCorrectRequestAndBookFound_thenResponseShouldBeOkAndBookDeleted() {
		final String uuid = "c4ca4238-a0b9-3382-8dcc-509a6f75849b";
		final String resourceLocation = "/books/" + uuid;
		Response response = target(resourceLocation).request().delete();

		Optional<Book> actualBook = appContext.getBean(BookRepository.class).getBook(UUID.fromString(uuid));

		assertTrue(actualBook.isEmpty());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

	@Test
	@DisplayName("report Not Modified status for wrong DELETE request and book should not deleted")
	void givenDeleteBook_whenIncorrectRequestOrBookNotFound_thenResponseShouldBeNotModifiedAndBookNotDeleted() {
		final String wrongUuid = "c4ca4238-a0b9-3382-8dcc-509a6f75849a";
		final String uuid = "c4ca4238-a0b9-3382-8dcc-509a6f75849b";
		final String resourceLocation = "/books/" + wrongUuid;

		Book originalBook = appContext.getBean(BookRepository.class).getBook(UUID.fromString(uuid)).get();
		Response response = target(resourceLocation).request().delete();

		Optional<Book> actualBook = appContext.getBean(BookRepository.class).getBook(UUID.fromString(uuid));

		assertFalse(actualBook.isEmpty());
		assertEquals(originalBook, actualBook.get());
		assertEquals(Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

	@Test
	@DisplayName("report CREATED status for correct POST request and book should be added")
	void givenAddBook_whenCorrectRequestAndBookNotFound_thenResponseShouldBeOkAndBookAdded() {
		final UUID uuid = UUID.nameUUIDFromBytes("4".getBytes());
		final String resourceLocation = "/books";

		Book expectedBook = Book.builder().id(uuid).author("Rocky Balboa").title("Rambo").isbn("77777")
				.publishDate(LocalDate.of(1995, 1, 1)).genre(Genre.HISTORICAL).country("USA").language("English")
				.pageCount(30).size(Size.QUARTO).cover(new Cover(Cover.Type.SOFT, Cover.Surface.GLOSS)).build();
		Response response = target(resourceLocation).request()
				.post(Entity.entity(expectedBook, MediaType.APPLICATION_JSON));

		Optional<Book> actualBook = appContext.getBean(BookRepository.class).getBook(uuid);

		assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNotNull(response.getLocation());
		assertFalse(actualBook.isEmpty());
		assertEquals(expectedBook, actualBook.get());
	}

	@Test
	@DisplayName("book should not be added if book with same id exists")
	void givenAddBook_whenCorrectRequestAndBookFound_thenBookShouldNotBeAdded() {
		final UUID uuid = UUID.nameUUIDFromBytes("1".getBytes());
		final String resourceLocation = "/books";

		Book expectedBook = Book.builder().id(uuid).author("Rocky Balboa").title("Rambo").isbn("77777")
				.publishDate(LocalDate.of(1995, 1, 1)).genre(Genre.HISTORICAL).country("USA").language("English")
				.pageCount(30).size(Size.QUARTO).cover(new Cover(Cover.Type.SOFT, Cover.Surface.GLOSS)).build();

		int originalSize = appContext.getBean(BookRepository.class).getAllBooks().size();

		Response response = target(resourceLocation).request()
				.post(Entity.entity(expectedBook, MediaType.APPLICATION_JSON));

		int actualSize = appContext.getBean(BookRepository.class).getAllBooks().size();

		assertEquals(originalSize, actualSize);
		assertNotNull(response);
		assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

}
