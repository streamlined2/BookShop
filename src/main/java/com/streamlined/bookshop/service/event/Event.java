package com.streamlined.bookshop.service.event;

import java.time.Instant;
import java.util.UUID;

public interface Event {

	Instant instant();

	UUID requestId();

}
