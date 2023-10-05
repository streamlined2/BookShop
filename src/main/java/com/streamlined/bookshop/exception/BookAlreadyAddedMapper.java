package com.streamlined.bookshop.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BookAlreadyAddedMapper implements ExceptionMapper<BookAlreadyAddedException> {

	@Override
	public Response toResponse(BookAlreadyAddedException exception) {
		return Response.status(Response.Status.NOT_ACCEPTABLE).build();
	}

}
