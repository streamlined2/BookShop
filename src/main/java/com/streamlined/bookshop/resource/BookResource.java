package com.streamlined.bookshop.resource;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import com.streamlined.bookshop.exception.NoBookFoundException;
import com.streamlined.bookshop.model.book.BookDto;
import com.streamlined.bookshop.service.book.BookService;

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
	public List<BookDto> getAllBooks() {
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
		var updatedBook = bookService.updateBook(book, id);
		return updatedBook.isPresent() ? Response.ok().build() : Response.noContent().build();
	}

	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteBook(@PathParam("id") UUID id) {
		var deletedBook = bookService.deleteBook(id);
		return deletedBook.isPresent() ? Response.ok().build() : Response.noContent().build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addBook(BookDto bookDto, @Context UriInfo uriInfo) {
		var newBook = bookService.addBook(bookDto);
		return newBook.isPresent() ? Response.created(getResourceLocation(uriInfo, newBook.get())).build()
				: Response.noContent().build();
	}

	private URI getResourceLocation(UriInfo uriInfo, BookDto book) {
		return URI.create("%s/%s".formatted(uriInfo.getAbsolutePath().toString(), book.id().toString()));
	}

}
