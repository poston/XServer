package org.xserver.component.extension.shiro.exception;

import org.xserver.component.exception.AbstractServerError;

public class SessionLoginError extends AbstractServerError {

	private static final long serialVersionUID = -8098269500124759892L;

	public SessionLoginError(String username, String message) {
		super("Login occer error, the username[" + username
				+ "], " + message);
	}
}
