package com.streamlined.bookshop.service.event;

import java.util.UUID;

public abstract class RequestEvent extends Event {

	protected UUID requestId;

	protected RequestEvent() {
		requestId = UUID.randomUUID();
	}

	public UUID getRequestId() {
		return requestId;
	}

	public void setRequestId(UUID requestId) {
		this.requestId = requestId;
	}

}
