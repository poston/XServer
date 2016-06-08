package org.xserver.component.core.http.session.exception;

/**
 * Exception thrown when attempting to interact with the system under an
 * established session when that session is considered invalid. The meaning of
 * the term 'invalid' is based on application behavior. For example, a Session
 * is considered invalid if it has been explicitly stopped (e.g. when a user
 * logs-out or when explicitly {@link Session#stop() stopped} programmatically.
 * A Session can also be considered invalid if it has expired.
 *
 * @since 0.1
 */
public class InvalidSessionException extends SessionException {

	private static final long serialVersionUID = -1680549107529374017L;

	/**
	 * Creates a new InvalidSessionException.
	 */
	public InvalidSessionException() {
		super();
	}

	/**
	 * Constructs a new InvalidSessionException.
	 *
	 * @param message
	 *            the reason for the exception
	 */
	public InvalidSessionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new InvalidSessionException.
	 *
	 * @param cause
	 *            the underlying Throwable that caused this exception to be
	 *            thrown.
	 */
	public InvalidSessionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new InvalidSessionException.
	 *
	 * @param message
	 *            the reason for the exception
	 * @param cause
	 *            the underlying Throwable that caused this exception to be
	 *            thrown.
	 */
	public InvalidSessionException(String message, Throwable cause) {
		super(message, cause);
	}

}
