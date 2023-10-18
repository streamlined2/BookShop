package com.streamlined.bookshop.model.book;

public record Cover(Type type, Surface surface) {
	public enum Type {
		HARD, SOFT
	}

	public enum Surface {
		UNCOATED, SILK, GLOSS
	}
}
