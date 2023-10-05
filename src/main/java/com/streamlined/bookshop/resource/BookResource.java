package com.streamlined.bookshop.resource;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import com.streamlined.bookshop.dao.BookRepository;
import com.streamlined.bookshop.exception.NoBookFoundException;
import com.streamlined.bookshop.model.Book;

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
	private BookRepository bookRepository;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Book> getAllBooks() {
		return bookRepository.getAllBooks();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Book getBook(@PathParam("id") UUID id) {
		return bookRepository.getBook(id)
				.orElseThrow(() -> new NoBookFoundException("no book found with id %s".formatted(id.toString())));
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateBook(Book book, @PathParam("id") UUID id) {
		var updated = bookRepository.updateBook(book, id);
		return (updated ? Response.ok() : Response.notModified()).build();
	}

	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteBook(@PathParam("id") UUID id) {
		var deleted = bookRepository.deleteBook(id);
		return (deleted ? Response.ok() : Response.notModified()).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addBook(Book book, @Context UriInfo uriInfo) {
		bookRepository.addBook(book);
		return Response.status(Response.Status.CREATED.getStatusCode())
				.header("Location", getResourceLocation(uriInfo, book)).build();
	}

	private String getResourceLocation(UriInfo uriInfo, Book book) {
		return "%s/%s".formatted(uriInfo.getAbsolutePath().toString(), book.getId().toString());
	}

}
