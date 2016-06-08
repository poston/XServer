package org.xserver.component.extension.shiro.exception;

import org.xserver.component.exception.AbstractServerError;

public class SessionInvalidError extends AbstractServerError {

	private static final long serialVersionUID = -2907470220696081148L;

	public SessionInvalidError(String session, String message) {
		super("The session \"" + session + "\" valid error, " + message);
	}
}
