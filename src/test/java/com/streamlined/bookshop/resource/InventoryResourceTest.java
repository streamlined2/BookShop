package com.streamlined.bookshop.resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.streamlined.bookshop.config.database.MongoDBInventoryBeforeConvertCallbackComponent;
import com.streamlined.bookshop.dao.InventoryRepository;
import com.streamlined.bookshop.exception.NoInventoryFoundException;
import com.streamlined.bookshop.model.inventory.InventoryMapper;
import com.streamlined.bookshop.service.inventory.DefaultInventoryService;
import com.streamlined.bookshop.service.inventory.InventoryService;

import jakarta.ws.rs.core.Application;

class InventoryResourceTest extends JerseyTest {

	private InventoryRepository inventoryRepository;
	private InventoryMapper inventoryMapper;
	private InventoryService inventoryService;

	private AnnotationConfigApplicationContext appContext;

	@Override
	protected Application configure() {
		inventoryRepository = mock(InventoryRepository.class);
		inventoryMapper = new InventoryMapper();
		inventoryService = new DefaultInventoryService(inventoryRepository, inventoryMapper);
		appContext = new AnnotationConfigApplicationContext(MongoDBInventoryBeforeConvertCallbackComponent.class,
				InventoryMapper.class);
		appContext.registerBean(InventoryService.class, () -> inventoryService);

		final ResourceConfig config = new ResourceConfig(InventoryResource.class, NoInventoryFoundException.class);
		config.property("contextConfig", appContext);
		return config;
	}

	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
	}

	@AfterEach
	public void tearDown() throws Exception {
		Mockito.reset(inventoryRepository);
		super.tearDown();
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
