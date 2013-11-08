package org.xserver.component.exception;

public class InvalidArgumentError extends AbstractServerError {
	private static final long serialVersionUID = -7465073137244957441L;

	public InvalidArgumentError(String argument, String message) {
		super("Argument \"" + argument + "\" is invalid, " + message);
	}
}
