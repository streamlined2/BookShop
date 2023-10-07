package com.streamlined.bookshop.resource;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.streamlined.bookshop.config.MongoDBBeforeConvertCallbackComponent;
import com.streamlined.bookshop.exception.BookAlreadyAddedMapper;
import com.streamlined.bookshop.exception.NoBookFoundMapper;
import com.streamlined.bookshop.model.BookDto;
import com.streamlined.bookshop.model.BookMapper;
import com.streamlined.bookshop.model.Cover;
import com.streamlined.bookshop.model.Genre;
import com.streamlined.bookshop.model.Size;
import com.streamlined.bookshop.service.BookService;
import com.streamlined.bookshop.service.DefaultBookService;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

class BookResourceTest extends JerseyTest {
	
	private AnnotationConfigApplicationContext appContext;

	@Override
	protected Application configure() {
		appContext = new AnnotationConfigApplicationContext(MongoDBBeforeConvertCallbackComponent.class,
				BookMapper.class, DefaultBookService.class);
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
		List<BookDto> expectedContent = getBookService().getAllBooks().toList();

		Response response = target(resourceLocation).request().get();

		List<BookDto> content = List.of(response.readEntity(BookDto[].class));

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		assertNull(response.getLocation());
		assertEquals(expectedContent, content);
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
		final UUID uuid = UUID.nameUUIDFromBytes("1".getBytes());
		final String resourceLocation = "/books/" + uuid.toString();
		BookDto expectedBook = getBookService().getBook(uuid).get();

		Response response = target(resourceLocation).request().get();

		BookDto actualBook = response.readEntity(BookDto.class);

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		assertNull(response.getLocation());
		assertEquals(expectedBook, actualBook);
	}

	@Test
	@DisplayName("report book not found if id is wrong")
	void givenGetBook_whenCorrectRequestAndBookNotFound_thenResponseShouldBeNotFound() {
		final UUID uuid = UUID.nameUUIDFromBytes("-1".getBytes());
		final String resourceLocation = "/books/" + uuid.toString();

		Response response = target(resourceLocation).request().get();

		assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

	@Test
	@DisplayName("report OK status for correct PUT request and update book")
	void givenUpdateBook_whenCorrectRequestAndBookFound_thenResponseShouldBeOkAndBookUpdated() {
		final UUID uuid = UUID.nameUUIDFromBytes("1".getBytes());
		final String resourceLocation = "/books/" + uuid.toString();

		BookDto expectedBook = BookDto.builder().id(uuid).author("Jennifer Peterson").title("Tales of witch")
				.isbn("23456").publishDate(LocalDate.of(2000, 1, 1)).genre(Genre.FICTIONAL).country("Britain")
				.language("English").pageCount(100).size(Size.DUODECIMO)
				.cover(new Cover(Cover.Type.HARD, Cover.Surface.GLOSS)).build();

		Response response = target(resourceLocation).request()
				.put(Entity.entity(expectedBook, MediaType.APPLICATION_JSON));

		BookDto actualBook = getBookService().getBook(uuid).get();

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
		assertEquals(expectedBook, actualBook);
	}

	@Test
	@DisplayName("report Not Modified status if incorrect PUT request and book should remain unchanged")
	void givenUpdateBook_whenIncorrectRequestOrBookNotFound_thenResponseShouldBeNotModifiedAndBookRemainsUnchanged() {
		final UUID uuid = UUID.nameUUIDFromBytes("1".getBytes());
		final UUID wrongUuid = UUID.nameUUIDFromBytes("-1".getBytes());
		final String resourceLocation = "/books/" + wrongUuid.toString();

		BookDto newBook = BookDto.builder().id(uuid).author("Jennifer Peterson").title("Tales of witch").isbn("23456")
				.publishDate(LocalDate.of(2000, 1, 1)).genre(Genre.FICTIONAL).country("Britain").language("English")
				.pageCount(100).size(Size.DUODECIMO).cover(new Cover(Cover.Type.HARD, Cover.Surface.GLOSS)).build();
		BookDto originalBook = getBookService().getBook(uuid).get();

		Response response = target(resourceLocation).request().put(Entity.entity(newBook, MediaType.APPLICATION_JSON));

		BookDto actualBook = getBookService().getBook(uuid).get();

		assertEquals(Status.NOT_MODIFIED.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
		assertEquals(originalBook, actualBook);
	}

	@Test
	@DisplayName("report OK status for correct DELETE request and book should be deleted")
	void givenDeleteBook_whenCorrectRequestAndBookFound_thenResponseShouldBeOkAndBookDeleted() {
		final UUID uuid = UUID.nameUUIDFromBytes("1".getBytes());
		final String resourceLocation = "/books/" + uuid.toString();

		Response response = target(resourceLocation).request().delete();

		Optional<BookDto> actualBook = getBookService().getBook(uuid);

		assertTrue(actualBook.isEmpty());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

	@Test
	@DisplayName("report Not Modified status for wrong DELETE request and book should not deleted")
	void givenDeleteBook_whenIncorrectRequestOrBookNotFound_thenResponseShouldBeNotModifiedAndBookNotDeleted() {
		final UUID wrongUuid = UUID.nameUUIDFromBytes("-1".getBytes());
		final UUID uuid = UUID.nameUUIDFromBytes("1".getBytes());
		final String resourceLocation = "/books/" + wrongUuid.toString();
		BookDto originalBook = getBookService().getBook(uuid).get();

		Response response = target(resourceLocation).request().delete();

		Optional<BookDto> actualBook = getBookService().getBook(uuid);

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
		BookDto expectedBook = BookDto.builder().id(uuid).author("Rocky Balboa").title("Rambo").isbn("77777")
				.publishDate(LocalDate.of(1995, 1, 1)).genre(Genre.HISTORICAL).country("USA").language("English")
				.pageCount(30).size(Size.QUARTO).cover(new Cover(Cover.Type.SOFT, Cover.Surface.GLOSS)).build();

		Response response = target(resourceLocation).request()
				.post(Entity.entity(expectedBook, MediaType.APPLICATION_JSON));

		Optional<BookDto> actualBook = getBookService().getBook(uuid);

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
		BookDto newBook = BookDto.builder().id(uuid).author("Rocky Balboa").title("Rambo").isbn("77777")
				.publishDate(LocalDate.of(1995, 1, 1)).genre(Genre.HISTORICAL).country("USA").language("English")
				.pageCount(30).size(Size.QUARTO).cover(new Cover(Cover.Type.SOFT, Cover.Surface.GLOSS)).build();
		long originalSize = getBookService().getAllBooks().count();

		Response response = target(resourceLocation).request().post(Entity.entity(newBook, MediaType.APPLICATION_JSON));

		long actualSize = getBookService().getAllBooks().count();

		assertEquals(originalSize, actualSize);
		assertNotNull(response);
		assertEquals(Status.NOT_ACCEPTABLE.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

	private BookService getBookService() {
		return appContext.getBean(BookService.class);
	}

}
