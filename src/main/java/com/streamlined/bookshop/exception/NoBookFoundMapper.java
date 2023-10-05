package com.streamlined.bookshop.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NoBookFoundMapper implements ExceptionMapper<NoBookFoundException> {

	@Override
	public Response toResponse(NoBookFoundException exception) {
		return Response.status(Response.Status.NOT_FOUND).build();
	}

}
