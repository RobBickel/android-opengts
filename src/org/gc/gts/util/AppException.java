package org.gc.gts.util;

public class AppException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public AppException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public AppException(String detailMessage) {
		super(detailMessage);
	}
}
