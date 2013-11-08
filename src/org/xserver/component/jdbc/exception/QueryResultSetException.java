package org.xserver.component.jdbc.exception;

/**
 * The subclass of {@link DataAccessException}, using<code>jdbcTemplate</code>
 * to operate data base, when reflect to get result error, the exception throw
 * 
 * @author postonzhang
 * 
 */
public class QueryResultSetException extends DataAccessException {

	private static final long serialVersionUID = 856765211270575612L;

	public QueryResultSetException(String msg) {
		super(msg);
	}

	public QueryResultSetException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
