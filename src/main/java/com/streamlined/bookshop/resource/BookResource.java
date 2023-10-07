package com.streamlined.bookshop.resource;

import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import com.streamlined.bookshop.exception.NoBookFoundException;
import com.streamlined.bookshop.model.BookDto;
import com.streamlined.bookshop.service.BookService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Path("/books")
@AllArgsConstructor
@NoArgsConstructor
public class BookResource {

	@Autowired
	private BookService bookService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Stream<BookDto> getAllBooks() {
		return bookService.getAllBooks();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public BookDto getBook(@PathParam("id") UUID id) {
		return bookService.getBook(id)
				.orElseThrow(() -> new NoBookFoundException("no book found with id %s".formatted(id.toString())));
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateBook(BookDto book, @PathParam("id") UUID id) {
		var updated = bookService.updateBook(book, id);
		return (updated ? Response.ok() : Response.notModified()).build();
	}

	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteBook(@PathParam("id") UUID id) {
		var deleted = bookService.deleteBook(id);
		return (deleted ? Response.ok() : Response.notModified()).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addBook(BookDto book, @Context UriInfo uriInfo) {
		bookService.addBook(book);
		return Response.status(Response.Status.CREATED.getStatusCode())
				.header("Location", getResourceLocation(uriInfo, book)).build();
	}

	private String getResourceLocation(UriInfo uriInfo, BookDto book) {
		return "%s/%s".formatted(uriInfo.getAbsolutePath().toString(), book.id().toString());
	}

}
