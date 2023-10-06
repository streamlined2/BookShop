package com.streamlined.bookshop;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

class ExternalResourceTest {

	private static final String CONTEXT_RESOURCE_URL = "/appcontext/resources";
	private static final String SERVER_URL = "localhost";
	private static final int SERVER_PORT = 9191;
	private static final int STATUS_OK = 200;

	private final WireMockServer server = new WireMockServer(SERVER_PORT);
	private final HttpClient client = HttpClient.newHttpClient();

	@BeforeEach
	void setUp() throws Exception {
		server.start();
		configureFor(SERVER_URL, SERVER_PORT);
	}

	@AfterEach
	void tearDown() throws Exception {
		server.stop();
		server.resetAll();
	}

	@Test
	void testGetResource() throws IOException, InterruptedException {
		String resourceUrl = "/resource";
		String body = "response";
		stubFor(get(getResourceUrl(resourceUrl))
				.willReturn(ok().withHeader("Content-Type", "application/json").withBody(body)));
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getRequestUrl(resourceUrl)))
				.header("Content-Type", "application/json").GET().build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		verify(getRequestedFor(urlEqualTo(getResourceUrl(resourceUrl))));
		assertEquals(STATUS_OK, response.statusCode());
		assertEquals(body, response.body());
	}

	private static String getRequestUrl(String url) {
		return "http://%s:%d%s%s".formatted(SERVER_URL, SERVER_PORT, CONTEXT_RESOURCE_URL, url);
	}

	private static String getResourceUrl(String url) {
		return "%s%s".formatted(CONTEXT_RESOURCE_URL, url);
	}

}
