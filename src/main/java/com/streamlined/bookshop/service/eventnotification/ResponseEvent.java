package com.streamlined.bookshop.service.eventnotification;

import java.time.Instant;
import java.util.UUID;

public interface ResponseEvent {

	OperationStatus getOperationStatus();

	Instant getInstant();

	UUID getRequestId();

}
