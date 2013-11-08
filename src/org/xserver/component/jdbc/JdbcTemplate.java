package org.xserver.component.jdbc;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.component.annotation.Alias;
import org.xserver.component.annotation.Extended;
import org.xserver.component.jdbc.exception.DataAccessException;
import org.xserver.component.jdbc.exception.QueryResultSetException;
import org.xserver.component.jdbc.util.JdbcUtils;

/**
 * The <code>JdbcTemplate</code> aim at reducing the difficulty when using JDBC.
 * When use <code>JdbcTemplate</code> most should configure properties in
 * <code>classpath:jdbcContext.xml</code>.
 * 
 * @author postonzhang
 * @since 2013/05/06
 */
public class JdbcTemplate implements JdbcOperations {

	private static final Logger log = LoggerFactory
			.getLogger(JdbcTemplate.class);
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public JdbcTemplate(DataSource dataSource) throws SQLException {
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		return getDataSource().getConnection();
	}

	public void execute(final String sql, final Object... args)
			throws DataAccessException {
		Connection con = null;
		PreparedStatement pst = null;
		if (log.isDebugEnabled()) {
			log.debug("Executing SQL [" + JdbcUtils.placeholder(sql, args)
					+ "]");
		}

		try {
			con = getConnection();
			pst = con.prepareStatement(sql);
			JdbcUtils.setPreparedStatementParams(pst, args);
			pst.execute();
		} catch (Exception e) {
			log.error("Execute SQL [" + JdbcUtils.placeholder(sql, args)
					+ "] error", e);
		} finally {
			JdbcUtils.release(pst);
			JdbcUtils.release(con);
		}
	}

	@Override
	public <T> T queryForObject(final String sql, Class<T> clazz,
			final Object... args) throws DataAccessException {
		List<T> result = queryForList(sql, clazz, args);
		if (result.size() == 1) {
			return result.get(0);
		} else if (result.size() == 0) {
			return null;
		} else {
			new QueryResultSetException("Execute SQL ["
					+ JdbcUtils.placeholder(sql, args)
					+ "] found multiple result for " + clazz.getName());
		}

		return null;
	}

	@Override
	public int queryForInt(final String sql, final Object... args)
			throws DataAccessException {
		Object result = queryOneResult(sql, args);
		if (result != null
				&& !result.getClass().isAssignableFrom(Integer.class)) {
			log.error("query result type is {}, cannot change to Integer",
					result.getClass());
		}

		return result == null ? -1 : Integer.valueOf(result.toString());
	}

	@Override
	public long queryForLong(final String sql, final Object... args)
			throws DataAccessException {
		Object result = queryOneResult(sql, args);
		if (result != null && !result.getClass().isAssignableFrom(Long.class)) {
			log.error("query result type is {}, cannot change to Long",
					result.getClass());
		}
		return result == null ? -1 : Long.valueOf(result.toString());
	}

	@Override
	public float queryForFloat(final String sql, final Object... args)
			throws DataAccessException {
		Object result = queryOneResult(sql, args);
		if (result != null && !result.getClass().isAssignableFrom(Float.class)) {
			log.error("query result type is {}, cannot change to Float",
					result.getClass());
		}
		return result == null ? -1 : Float.valueOf(result.toString());
	}

	@Override
	public double queryForDouble(final String sql, final Object... args)
			throws DataAccessException {
		Object result = queryOneResult(sql, args);
		if (result != null && !result.getClass().isAssignableFrom(Double.class)) {
			log.error("query result type is {}, cannot change to Double",
					result.getClass());
		}
		return result == null ? -1 : Double.valueOf(result.toString());
	}

	@Override
	public <T> List<T> queryForList(final String sql, Class<T> clazz,
			final Object... args) throws DataAccessException {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<T> result = new ArrayList<T>();

		if (log.isDebugEnabled()) {
			log.debug("Executing SQL query ["
					+ JdbcUtils.placeholder(sql, args) + "]");
		}

		try {
			con = getConnection();
			pst = con.prepareStatement(sql);
			JdbcUtils.setPreparedStatementParams(pst, args);
			rs = pst.executeQuery();
			try {
				while (rs.next()) {
					T obj = JdbcUtils.reflectResultSet(rs, clazz);
					result.add(obj);
				}
			} catch (Exception e) {
				throw new QueryResultSetException("reflect " + clazz.getName()
						+ " error", e);
			}
		} catch (Exception e) {
			throw new QueryResultSetException("Execute SQL ["
					+ JdbcUtils.placeholder(sql, args) + "] error", e);
		} finally {
			JdbcUtils.release(rs);
			JdbcUtils.release(pst);
			JdbcUtils.release(con);
		}

		return result;
	}

	@Override
	public <T> List<Map<String, Object>> queryForList(final String sql,
			final Object... args) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param sql
	 *            the SQL template
	 * @param args
	 *            the SQL arguments
	 * @return
	 */
	private Object queryOneResult(final String sql, final Object... args) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Object result = null;

		if (log.isDebugEnabled()) {
			log.debug("Executing SQL query ["
					+ JdbcUtils.placeholder(sql, args) + "]");
		}

		try {
			con = getConnection();
			pst = con.prepareStatement(sql);
			JdbcUtils.setPreparedStatementParams(pst, args);
			rs = pst.executeQuery();
			if (rs.next()) {
				result = rs.getObject(1);
			}
		} catch (SQLException e) {
			new QueryResultSetException("Execute SQL ["
					+ JdbcUtils.placeholder(sql, args) + "] error", e);
		} finally {
			JdbcUtils.release(pst);
			JdbcUtils.release(rs);
			JdbcUtils.release(con);
		}

		return result;

	}

	/**
	 * Batch execute sql.
	 * 
	 * @param sql
	 *            the SQL template
	 * @param args
	 *            batch execute arguments
	 */
	public void executeBatch(final String sql, final List<Object[]> args)
			throws DataAccessException {
		Connection con = null;
		PreparedStatement pst = null;

		if (log.isDebugEnabled()) {
			for (Object[] objs : args) {
				log.debug("Executing SQL [" + JdbcUtils.placeholder(sql, objs)
						+ "]");
			}
		}

		try {
			con = getConnection();
			pst = con.prepareStatement(sql);

			con.setAutoCommit(false);

			for (int i = 0; i < args.size(); i++) {
				JdbcUtils.setPreparedStatementParams(pst, args.get(i));
				pst.addBatch();
			}

			pst.executeBatch();

			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				log.error("RollBack error", ex);
			}
			log.error("ExecuteBatch SQL [" + sql + "] error", e);
		} finally {
			JdbcUtils.release(pst);
			JdbcUtils.release(con);
		}
	}

	/**
	 * Insert into table.
	 * 
	 * @param tableName
	 *            the table that datum should insert into
	 * @param t
	 *            a javaBean
	 */
	public <T> void insert(final String tableName, final T t) {
		try {
			Field[] fields = t.getClass().getDeclaredFields();
			StringBuffer template = new StringBuffer("INSERT INTO ");
			ArrayList<Object> args = new ArrayList<Object>();

			template.append(tableName).append("(");
			int i = 0;
			for (Field field : fields) {
				if (field.getAnnotation(Extended.class) == null) {
					Alias alias = field.getAnnotation(Alias.class);
					String fieldName = alias != null ? alias.value() : field
							.getName();
					template.append(fieldName).append(",");
					field.setAccessible(true);
					args.add(field.get(t));
					i++;
				}
			}
			template.deleteCharAt(template.length() - 1).append(") ")
					.append("VALUES(");

			for (int j = 0; j < i; j++) {
				template.append("?,");
			}
			template.deleteCharAt(template.length() - 1).append(")");

			execute(template.toString(), args.toArray());
		} catch (Exception e) {
			// should not go there
			log.error("Insert Error", e);
		}
	}

	/**
	 * Batch insert specialized table.
	 * 
	 * @param tableName
	 *            the table that datum should insert into
	 * @param list
	 *            the list of JavaBean
	 */
	public <T> void insertBatch(final String tableName, List<T> list) {
		if (list == null || list.size() == 0) {
			new IllegalArgumentException(
					"Argument List should not be null or empty.");
		}

		StringBuffer template = new StringBuffer("INSERT INTO ");
		template.append(tableName).append("(");

		T first = list.get(0);
		Field[] fields = first.getClass().getDeclaredFields();
		List<Object[]> args = new ArrayList<Object[]>();

		try {
			int i = 0;
			for (Field field : fields) {
				if (field.getAnnotation(Extended.class) == null) {
					Alias alias = field.getAnnotation(Alias.class);
					String fieldName = alias != null ? alias.value() : field
							.getName();
					template.append(fieldName).append(",");
					i++;
				}
			}

			template.deleteCharAt(template.length() - 1).append(") ")
					.append("VALUES(");
			for (int j = 0; j < i; j++) {
				template.append("?,");
			}
			template.deleteCharAt(template.length() - 1).append(")");

			for (T t : list) {
				ArrayList<Object> arg = new ArrayList<Object>();

				for (Field field : fields) {
					if (field.getAnnotation(Extended.class) == null) {
						field.setAccessible(true);
						arg.add(field.get(t));
					}
				}

				args.add(arg.toArray());
			}
		} catch (Exception e) {
			// should not go there
			log.error("reflect set object field error", e);
		}

		executeBatch(template.toString(), args);
	}
}
