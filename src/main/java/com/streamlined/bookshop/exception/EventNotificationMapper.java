package com.streamlined.bookshop.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class EventNotificationMapper implements ExceptionMapper<EventNotificationException> {

	@Override
	public Response toResponse(EventNotificationException exception) {
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

}
