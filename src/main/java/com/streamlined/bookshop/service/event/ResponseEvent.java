package com.streamlined.bookshop.service.event;

public interface ResponseEvent extends Event {

	OperationStatus operationStatus();

}
