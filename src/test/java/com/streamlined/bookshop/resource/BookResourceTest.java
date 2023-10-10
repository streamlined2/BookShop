package com.streamlined.bookshop.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.streamlined.bookshop.config.MongoDBBeforeConvertCallbackComponent;
import com.streamlined.bookshop.dao.BookRepository;
import com.streamlined.bookshop.exception.BookAlreadyAddedMapper;
import com.streamlined.bookshop.exception.NoBookFoundMapper;
import com.streamlined.bookshop.model.Book;
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

	private final static UUID WRONG_UUID = UUID.nameUUIDFromBytes("-1".getBytes());
	private final static UUID NEWLY_ASSIGNED_UUID = UUID.nameUUIDFromBytes("999".getBytes());

	private BookRepository bookRepository;
	private BookMapper bookMapper;
	private BookService bookService;

	private AnnotationConfigApplicationContext appContext;

	@Override
	protected Application configure() {
		bookRepository = mock(BookRepository.class);
		bookMapper = new BookMapper();
		bookService = new DefaultBookService(bookRepository, bookMapper, List.of());
		appContext = new AnnotationConfigApplicationContext(MongoDBBeforeConvertCallbackComponent.class,
				BookMapper.class);
		appContext.registerBean(BookService.class, () -> bookService);

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
		Mockito.reset(bookRepository);
		super.tearDown();
	}

	@Test
	@DisplayName("return book list for correct GET request")
	void givenGetAllBooks_whenCorrectRequest_thenResponseShouldBeOkAndReturnListOfBooks() {
		final String resourceLocation = "/books";
		final var expectedBooks = getBookList();
		final var expectedBookDtos = bookMapper.toDtos(expectedBooks);
		when(bookRepository.findAll()).thenReturn(expectedBooks);

		Response response = target(resourceLocation).request().get();

		List<BookDto> actualBookDtos = List.of(response.readEntity(BookDto[].class));

		verify(bookRepository).findAll();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		assertNull(response.getLocation());
		assertEquals(expectedBookDtos, actualBookDtos);
	}

	private List<Book> getBookList() {
		return new ArrayList<>(List.of(
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
	}

	@Test
	@DisplayName("report not found if request URI to GET all boooks is wrong")
	void givenGetAllBooks_whenWrongRequestURI_thenResponseShouldBeNotFound() {
		final String resourceLocation = "/albums";

		Response response = target(resourceLocation).request().get();

		verify(bookRepository, Mockito.never()).findAll();
		assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

	@Test
	@DisplayName("report OK and return found book for correct GET request")
	void givenGetBook_whenCorrectRequestAndBookFound_thenResponseShouldBeOkAndReturnBook() {
		final var expectedBook = getBook();
		final var expectedBookDto = bookMapper.toDto(expectedBook);
		final String resourceLocation = "/books/" + expectedBook.getId().toString();
		when(bookRepository.findById(Mockito.<UUID>any())).thenReturn(Optional.of(expectedBook));

		Response response = target(resourceLocation).request().get();

		BookDto actualBookDto = response.readEntity(BookDto.class);

		verify(bookRepository).findById(expectedBook.getId());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		assertNull(response.getLocation());
		assertEquals(expectedBookDto, actualBookDto);
	}

	private Book getBook() {
		return Book.builder().id(UUID.nameUUIDFromBytes("1".getBytes())).author("Kyle Owl").title("Tales of magician")
				.isbn("77777").publishDate(LocalDate.of(2000, 1, 1)).genre(Genre.FICTIONAL).country("Britain")
				.language("English").pageCount(100).size(Size.DUODECIMO)
				.cover(new Cover(Cover.Type.HARD, Cover.Surface.GLOSS)).build();
	}

	@Test
	@DisplayName("report NOT FOUND if book not found")
	void givenGetBook_whenCorrectRequestAndBookNotFound_thenResponseShouldBeNotFound() {
		final String resourceLocation = "/books/" + WRONG_UUID.toString();
		when(bookRepository.findById(Mockito.<UUID>any())).thenReturn(Optional.empty());

		Response response = target(resourceLocation).request().get();

		verify(bookRepository).findById(WRONG_UUID);
		assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

	@Test
	@DisplayName("report OK status for PUT request and update existing book")
	void givenUpdateBook_whenCorrectRequestAndBookExists_thenResponseShouldBeOkAndBookUpdated() {
		final Book expectedBook = getBook();
		final String resourceLocation = "/books/" + expectedBook.getId().toString();
		final List<Book> actualBookHolder = new ArrayList<>();
		when(bookRepository.save(Mockito.<Book>any())).thenAnswer(new Answer<Book>() {
			@Override
			public Book answer(InvocationOnMock invocation) {
				Book entity = invocation.getArgument(0);
				actualBookHolder.add(entity);
				return entity;
			}
		});

		Response response = target(resourceLocation).request()
				.put(Entity.entity(expectedBook, MediaType.APPLICATION_JSON));

		verify(bookRepository).save(expectedBook);
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
		assertFalse(actualBookHolder.isEmpty());
		assertEquals(expectedBook, actualBookHolder.get(0));
	}

	@Test
	@DisplayName("report OK status for PUT request and save new book")
	void givenUpdateBook_whenCorrectRequestAndBookNotFound_thenResponseShouldBeOkAndBookCreated() {
		final Book expectedBook = getBook();
		expectedBook.setId(WRONG_UUID);
		final String resourceLocation = "/books/" + WRONG_UUID.toString();
		final List<Book> actualBookHolder = new ArrayList<>();
		when(bookRepository.save(Mockito.<Book>any())).thenAnswer(new Answer<Book>() {
			@Override
			public Book answer(InvocationOnMock invocation) {
				Book entity = invocation.getArgument(0);
				actualBookHolder.add(entity);
				return entity;
			}
		});

		Response response = target(resourceLocation).request()
				.put(Entity.entity(expectedBook, MediaType.APPLICATION_JSON));

		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
		verify(bookRepository).save(expectedBook);
		assertFalse(actualBookHolder.isEmpty());
		assertEquals(expectedBook, actualBookHolder.get(0));
	}

	@Test
	@DisplayName("report OK status for DELETE request and existing book should be deleted")
	void givenDeleteBook_whenCorrectRequestAndBookFound_thenResponseShouldBeOkAndBookDeleted() {
		final Book expectedBook = getBook();
		final String resourceLocation = "/books/" + expectedBook.getId().toString();
		final List<UUID> actualUUIDHolder = new ArrayList<>();
		doAnswer(new Answer<UUID>() {
			@Override
			public UUID answer(InvocationOnMock invocation) {
				actualUUIDHolder.add(invocation.getArgument(0));
				return null;
			}
		}).when(bookRepository).deleteById(Mockito.<UUID>any());

		Response response = target(resourceLocation).request().delete();

		verify(bookRepository).deleteById(expectedBook.getId());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
		assertFalse(actualUUIDHolder.isEmpty());
		assertEquals(expectedBook.getId(), actualUUIDHolder.get(0));
	}

	@Test
	@DisplayName("report OK status for DELETE request and non-existing book should not be deleted")
	void givenDeleteBook_whenIncorrectRequestAndBookNotFound_thenResponseShouldBeOkAndBookNotDeleted() {
		final String resourceLocation = "/books/" + WRONG_UUID.toString();
		final List<UUID> actualUUIDHolder = new ArrayList<>();
		doAnswer(new Answer<UUID>() {
			@Override
			public UUID answer(InvocationOnMock invocation) {
				actualUUIDHolder.add(invocation.getArgument(0));
				return null;
			}
		}).when(bookRepository).deleteById(Mockito.<UUID>any());

		Response response = target(resourceLocation).request().delete();

		verify(bookRepository).deleteById(WRONG_UUID);
		assertFalse(actualUUIDHolder.isEmpty());
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNull(response.getLocation());
	}

	@Test
	@DisplayName("report CREATED status for correct POST request and book should be added")
	void givenAddBook_whenCorrectRequestAndBookNotFound_thenResponseShouldBeOkAndBookAdded() {
		final String resourceLocation = "/books";
		Book expectedBook = getBook();
		expectedBook.setId(null);
		final List<Book> actualBookHolder = new ArrayList<>();
		when(bookRepository.insert(Mockito.<Book>any())).thenAnswer(new Answer<Book>() {
			@Override
			public Book answer(InvocationOnMock invocation) {
				Book book = invocation.getArgument(0);
				book.setId(NEWLY_ASSIGNED_UUID);
				actualBookHolder.add(book);
				return book;
			}
		});

		Response response = target(resourceLocation).request()
				.post(Entity.entity(expectedBook, MediaType.APPLICATION_JSON));

		verify(bookRepository).insert(Mockito.<Book>any());
		assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
		assertNull(response.getMediaType());
		assertNotNull(response.getLocation());
		assertFalse(actualBookHolder.isEmpty());
		assertNotNull(actualBookHolder.get(0).getId());
		expectedBook.setId(NEWLY_ASSIGNED_UUID);
		assertEquals(expectedBook, actualBookHolder.get(0));
	}

}
