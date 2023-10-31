package com.streamlined.bookshop.resource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.streamlined.bookshop.Utilities;
import com.streamlined.bookshop.exception.NoInventoryFoundException;
import com.streamlined.bookshop.model.inventory.InventoryDto;
import com.streamlined.bookshop.service.inventory.InventoryService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Path("/inventories")
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResource {

	@Autowired
	private InventoryService inventoryService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<InventoryDto> getAllInventories() {
		return inventoryService.getAllInventories();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public InventoryDto getInventory(@PathParam("id") UUID id) {
		return inventoryService.getInventory(id).orElseThrow(
				() -> new NoInventoryFoundException("no inventory found with id %s".formatted(id.toString())));
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateInventory(InventoryDto inventoryDto, @PathParam("id") UUID id) {
		var updatedInventory = inventoryService.updateInventory(inventoryDto, id);
		return updatedInventory.isPresent() ? Response.ok().build() : Response.noContent().build();
	}

	@PATCH
	@Path("/{id}/replenish/{amount}")
	public Response replenishInventory(@PathParam("id") UUID id, @PathParam("amount") BigInteger amount) {
		var updatedInventory = inventoryService.replenishInventory(id, amount);
		return updatedInventory.isPresent() ? Response.ok().build() : Response.noContent().build();
	}

	@PATCH
	@Path("/{id}/sell/{amount}")
	public Response sellInventory(@PathParam("id") UUID id, @PathParam("amount") BigInteger amount) {
		var updatedInventory = inventoryService.sellInventory(id, amount);
		return updatedInventory.isPresent() ? Response.ok().build() : Response.noContent().build();
	}

	@PATCH
	@Path("/{id}/assignprice/{price}")
	public Response assignPrice(@PathParam("id") UUID id, @PathParam("price") BigDecimal price) {
		var updatedInventory = inventoryService.assignPrice(id, price);
		return updatedInventory.isPresent() ? Response.ok().build() : Response.noContent().build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteInventory(@PathParam("id") UUID id) {
		var deletedInventory = inventoryService.deleteInventory(id);
		return deletedInventory.isPresent() ? Response.ok().build() : Response.noContent().build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addInventory(InventoryDto inventoryDto, @Context UriInfo uriInfo) {
		var newInventory = inventoryService.addInventory(inventoryDto);
		return newInventory.isPresent()
				? Response.created(Utilities.getResourceLocation(uriInfo, newInventory.get().id())).build()
				: Response.noContent().build();
	}

}
