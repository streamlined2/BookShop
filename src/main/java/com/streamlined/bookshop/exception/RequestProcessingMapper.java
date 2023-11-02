package com.streamlined.bookshop.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RequestProcessingMapper implements ExceptionMapper<RequestProcessingException> {

	@Override
	public Response toResponse(RequestProcessingException exception) {
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

}
