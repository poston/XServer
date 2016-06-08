package org.xserver.component.extension.shiro.exception;

import org.xserver.component.exception.AbstractServerError;

public class ShiroRealmCheckError extends AbstractServerError {

	private static final long serialVersionUID = -8098269500124759892L;

	public ShiroRealmCheckError(String session, String message) {
		super("The session\"" + session
				+ "\" check realm error, " + message);
	}
}
