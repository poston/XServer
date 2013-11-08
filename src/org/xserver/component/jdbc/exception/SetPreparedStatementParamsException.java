package org.xserver.component.jdbc.exception;

/**
 * The subclass of {@link DataAccessException}, using<code>jdbcTemplate</code>
 * to operate data base, when set PreparedStatement arguments error, the
 * exception throw
 * 
 * @author postonzhang
 * 
 */
public class SetPreparedStatementParamsException extends DataAccessException {
	private static final long serialVersionUID = 780322114072508906L;

	public SetPreparedStatementParamsException(String msg) {
		super(msg);
	}

	public SetPreparedStatementParamsException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
