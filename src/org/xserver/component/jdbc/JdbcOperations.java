package org.xserver.component.jdbc;

import java.util.List;
import java.util.Map;

import org.xserver.component.jdbc.exception.DataAccessException;

/**
 * Interface specifying a basic set of JDBC operations. Implemented by
 * {@link JdbcTemplate}. Not often used directly, but a useful option to enhance
 * testability, as it can easily be mocked or stubbed.
 * 
 * @author postonzhang
 * @see JdbcTemplate
 * @since 2013/05/06
 */
public interface JdbcOperations {
	/**
	 * Execute a JDBC data access operation
	 * 
	 * @param sql
	 *            operating SQL template
	 * @param args
	 *            operating SQL arguments
	 * @throws DataAccessException
	 */
	public void execute(final String sql, final Object... args)
			throws DataAccessException;

	/**
	 * Execute a JDBC data access operations
	 * 
	 * @param sql
	 *            operating SQL template
	 * @param args
	 *            operating SQL arguments
	 * @throws DataAccessException
	 */
	public void executeBatch(final String sql, final List<Object[]> args)
			throws DataAccessException;

	/**
	 * Execute SQL and reflect to return a special class instance
	 */
	public <T> T queryForObject(final String sql, Class<T> clazz,
			final Object... args) throws DataAccessException;

	/**
	 * Execute SQL and reflect to return an integer
	 */
	public int queryForInt(final String sql, final Object... args)
			throws DataAccessException;

	/**
	 * Execute SQL and reflect to return a long
	 */
	public long queryForLong(final String sql, final Object... args)
			throws DataAccessException;

	/**
	 * Execute SQL and reflect to return a float
	 */
	public float queryForFloat(final String sql, final Object... args)
			throws DataAccessException;

	/**
	 * Execute SQL and reflect to return a double
	 */
	public double queryForDouble(final String sql, final Object... args)
			throws DataAccessException;

	/**
	 * Execute SQL and reflect to return a list of special class instances
	 */
	public <T> List<T> queryForList(final String sql, Class<T> clazz,
			final Object... args) throws DataAccessException;

	/**
	 * Execute SQL and reflect to return a map
	 */
	public <T> List<Map<String, Object>> queryForList(final String sql,
			final Object... args) throws DataAccessException;
}
