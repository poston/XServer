package org.xserver.component.core.http.session.exception;

public class SessionException extends RuntimeException {

	private static final long serialVersionUID = -2545848679250538412L;

	/**
	 * Creates a new ShiroException.
	 */
	public SessionException() {
		super();
	}

	/**
	 * Constructs a new ShiroException.
	 *
	 * @param message
	 *            the reason for the exception
	 */
	public SessionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new ShiroException.
	 *
	 * @param cause
	 *            the underlying Throwable that caused this exception to be
	 *            thrown.
	 */
	public SessionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new ShiroException.
	 *
	 * @param message
	 *            the reason for the exception
	 * @param cause
	 *            the underlying Throwable that caused this exception to be
	 *            thrown.
	 */
	public SessionException(String message, Throwable cause) {
		super(message, cause);
	}
}
