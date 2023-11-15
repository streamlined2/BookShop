package com.streamlined.bookshop.service.event;

import java.util.Optional;

public abstract class ModificationRequestEvent<D,S> extends RequestEvent {

	public abstract Optional<D> executeUpdate(S service);

}
