package com.streamlined.bookshop.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import com.streamlined.bookshop.exception.BookAlreadyAddedMapper;
import com.streamlined.bookshop.exception.NoBookFoundMapper;
import com.streamlined.bookshop.resource.BookResource;

import jakarta.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/resources")
public class RestConfig extends ResourceConfig {

	public RestConfig() {
		register(BookResource.class);
		register(BookAlreadyAddedMapper.class);
		register(NoBookFoundMapper.class);
	}

}
