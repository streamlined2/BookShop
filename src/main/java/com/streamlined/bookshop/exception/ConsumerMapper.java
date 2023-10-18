package com.streamlined.bookshop.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConsumerMapper implements ExceptionMapper<ConsumerException> {

	@Override
	public Response toResponse(ConsumerException exception) {
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

}
