package com.streamlined.bookshop.service.event;

import java.util.Objects;
import java.util.UUID;

public final class ModificationResponseEvent<D> extends ResponseEvent {

	private D item;

	public ModificationResponseEvent() {
	}

	public ModificationResponseEvent(D item, UUID requestId, OperationStatus operationStatus) {
		super(requestId, operationStatus);
		this.item = item;
	}

	public D getItem() {
		return item;
	}

	public void setItem(D item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return "[Modification response: request id=%s, item=%s, instant=%s, status=%s]".formatted(
				Objects.toString(requestId), Objects.toString(item), Objects.toString(instant),
				Objects.toString(operationStatus));
	}

}
