package com.streamlined.bookshop.config;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class JacksonObjectMapper extends ObjectMapper {

	public JacksonObjectMapper() {
		registerModule(new JavaTimeModule());
	}

}
