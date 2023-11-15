package com.streamlined.bookshop.service.event;

import java.util.UUID;

public abstract class ResponseEvent extends Event {

	protected UUID requestId;
	protected OperationStatus operationStatus;
	
	protected ResponseEvent() {
	}

	protected ResponseEvent(UUID requestId, OperationStatus operationStatus) {
		this.requestId = requestId;
		this.operationStatus = operationStatus;
	}

	@Override
	public UUID getRequestId() {
		return requestId;
	}

	@Override
	public void setRequestId(UUID requestId) {
		this.requestId = requestId;
	}

	public OperationStatus getOperationStatus() {
		return operationStatus;
	}

	public void setOperationStatus(OperationStatus operationStatus) {
		this.operationStatus = operationStatus;
	}

}
