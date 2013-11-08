package org.xserver.component.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public class ResouceNotFoundError extends AbstractServerError {
	private static final long serialVersionUID = 1L;

	public ResouceNotFoundError(HttpResponseStatus status, String message) {
		super(status, message);
	}
}
