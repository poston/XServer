package org.xserver.component.exception;

public class ExpressionEvalError extends AbstractServerError {
	private static final long serialVersionUID = 2219931321671052569L;

	public ExpressionEvalError(String express, String message) {
		super("Expression \"" + express + "\" evaluate error, " + message);
	}

}
