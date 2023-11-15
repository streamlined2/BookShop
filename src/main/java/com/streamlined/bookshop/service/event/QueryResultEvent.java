package com.streamlined.bookshop.service.event;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class QueryResultEvent<D> extends ResponseEvent {

	private List<D> list;

	public QueryResultEvent() {
	}

	public QueryResultEvent(List<D> list, UUID requestId, OperationStatus operationStatus) {
		super(requestId, operationStatus);
		this.list = list;
	}

	public List<D> getList() {
		return list;
	}

	public void setList(List<D> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "[Query result: request id=%s, list=%s, instant=%s, status=%s]".formatted(Objects.toString(requestId),
				Objects.toString(list), Objects.toString(instant), Objects.toString(operationStatus));
	}

}
