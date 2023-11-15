package com.streamlined.bookshop.service.event;

import java.util.List;

public abstract class QueryRequestEvent<D, S> extends RequestEvent {

	public abstract List<D> executeQuery(S service);

}
