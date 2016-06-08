package org.xserver.component.extension.shiro.exception;

import org.xserver.component.exception.AbstractServerError;

public class SessionNullError extends AbstractServerError {

	private static final long serialVersionUID = -2907470220696081148L;

	public SessionNullError(String message) {
		super("The session is null, " + message);
	}
}
