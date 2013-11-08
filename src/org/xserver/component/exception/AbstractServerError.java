package org.xserver.component.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public class AbstractServerError extends Error {

	private static final long serialVersionUID = 1L;
	private HttpResponseStatus status;

	public AbstractServerError() {
	}

	public AbstractServerError(String message) {
		super(message);
	}

	public AbstractServerError(HttpResponseStatus status, String message) {
		this(message);
		this.status = status;
	}

	@Override
	public String toString() {
		return getMessage();
	}

	public HttpResponseStatus getStatus() {
		return status;
	}
}
