package com.streamlined.bookshop.service.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public abstract class Event {

	protected Instant instant;

	protected Event() {
		instant = Instant.now();
	}

	public Instant getInstant() {
		return instant;
	}

	public void setInstant(Instant instant) {
		this.instant = instant;
	}

	public abstract UUID getRequestId();

	public abstract void setRequestId(UUID requestId);

	@Override
	public boolean equals(Object o) {
		if (o instanceof Event e) {
			return Objects.equals(getRequestId(), e.getRequestId()) && Objects.equals(getInstant(), e.getInstant());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getRequestId(), getInstant());
	}

}
