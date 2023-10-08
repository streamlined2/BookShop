package com.streamlined.bookshop.model;

import java.util.ArrayList;
import java.util.List;

public interface Mapper<E, D> {

	E toEntity(D dto);

	D toDto(E entity);

	default List<D> toDtos(List<E> entities) {
		var list = new ArrayList<D>(entities.size());
		for (var entity : entities) {
			list.add(toDto(entity));
		}
		return list;
	}

	default List<E> toEntities(List<D> dtos) {
		var list = new ArrayList<E>(dtos.size());
		for (var dto : dtos) {
			list.add(toEntity(dto));
		}
		return list;
	}

}
