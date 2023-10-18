package com.streamlined.bookshop;

import java.net.URI;
import java.util.UUID;

import jakarta.ws.rs.core.UriInfo;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utilities {

	public URI getResourceLocation(UriInfo uriInfo, UUID id) {
		return URI.create("%s/%s".formatted(uriInfo.getAbsolutePath().toString(), id.toString()));
	}

}
