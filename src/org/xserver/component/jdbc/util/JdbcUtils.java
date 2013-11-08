package org.xserver.component.jdbc.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.common.util.StringUtil;
import org.xserver.component.annotation.Alias;
import org.xserver.component.jdbc.exception.QueryResultSetException;
import org.xserver.component.jdbc.exception.SetPreparedStatementParamsException;

public class JdbcUtils {
	private static final Logger log = LoggerFactory.getLogger(JdbcUtils.class);
	private static Map<Class<?>, ReflectResultSet> simpleClass = initSimpleClass();

	private static String getColumnName(Field field) {
		Alias alias = field.getAnnotation(Alias.class);
		return alias == null ? field.getName() : alias.value();
	}

	private static Map<Class<?>, ReflectResultSet> initSimpleClass() {
		Map<Class<?>, ReflectResultSet> temp = new HashMap<Class<?>, ReflectResultSet>();

		ReflectResultSet rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getString(getColumnName(field));
			}
		};
		temp.put(String.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getByte(getColumnName(field));
			}
		};
		temp.put(Byte.class, rrs);
		temp.put(byte.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getShort(getColumnName(field));
			}
		};
		temp.put(Short.class, rrs);
		temp.put(short.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getBoolean(getColumnName(field));
			}
		};
		temp.put(Boolean.class, rrs);
		temp.put(boolean.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getInt(getColumnName(field));
			}
		};
		temp.put(Integer.class, rrs);
		temp.put(int.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getLong(getColumnName(field));
			}
		};
		temp.put(Long.class, rrs);
		temp.put(long.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getFloat(getColumnName(field));
			}
		};
		temp.put(Float.class, rrs);
		temp.put(float.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getDouble(getColumnName(field));
			}
		};
		temp.put(Double.class, rrs);
		temp.put(double.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getBigDecimal(getColumnName(field));
			}
		};
		temp.put(BigDecimal.class, rrs);
		temp.put(BigInteger.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getDate(getColumnName(field));
			}
		};
		temp.put(Date.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getTime(getColumnName(field));
			}
		};
		temp.put(Time.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(ResultSet rs, Field field) throws SQLException {
				return rs.getTimestamp(getColumnName(field));
			}
		};
		temp.put(Timestamp.class, rrs);

		return temp;
	}

	/**
	 * Set <code>PreparedStatement</code> parameters.
	 */
	public static void setPreparedStatementParams(final PreparedStatement pst,
			Object... args) {
		if (args == null) {
			return;
		}

		int index = 0;
		for (Object obj : args) {
			index++;
			try {
				pst.setObject(index, obj);
			} catch (SQLException e) {
				new SetPreparedStatementParamsException(
						"Set PreparedStatement parameter error", e);
			}
		}
	}

	/**
	 * Replace place holder with parameters
	 * 
	 * @param sql
	 *            the SQL template
	 * @param objs
	 *            the arguments
	 * @return
	 */
	public static String placeholder(final String sql, final Object... objs) {
		StringBuffer buffer = new StringBuffer();

		// when the placeholder char '?' at last, the split array length not
		// equal obj.length, so the sql add a blank space deal easily.
		String[] parts = (sql + " ").split("\\?");
		if (parts.length - 1 == objs.length) {
			int i;
			for (i = 0; i < objs.length; i++) {
				buffer.append(parts[i]);
				if (objs[i] instanceof String) {
					String arg = (String) objs[i];
					arg = arg.replace("'", "\'").replace("\n", "")
							.replace("\r", "").trim();
					if (arg.length() > 80) {
						arg = arg.substring(0, 10) + "..."
								+ arg.substring(arg.length() - 10);
					}
					buffer.append("'").append(arg).append("'");
				} else {
					buffer.append(objs[i]);
				}
			}

			buffer.append(parts[i]);
		}

		return buffer.toString();
	}

	public static <T> T reflectResultSet(ResultSet rs, Class<T> clazz)
			throws Exception {
		T obj = clazz.newInstance();
		Field[] fields = clazz.getDeclaredFields();
		if (StringUtil.isEmpty(fields)) {
			return null;
		}

		for (Field field : fields) {
			try {
				ReflectResultSet rrs = simpleClass.get(field.getType());
				if (rrs == null) {
					continue;
				}

				Object value = rrs.get(rs, field);
				field.setAccessible(true);
				field.set(obj, value);
			} catch (Exception e) {
				new QueryResultSetException("query for " + clazz.getName()
						+ "error when reflect field " + field.getName(), e);
			}
		}

		return obj;
	}

	/**
	 * Close the given JDBC Connection and ignore any thrown exception.
	 * 
	 * @param con
	 *            the JDBC Connection to close.
	 */
	public static void release(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.error("Could not close JDBC Connection", e);
			}
		}
	}

	/**
	 * Close the given JDBC PreparedStatement and ignore any thrown exception.
	 * 
	 * @param pst
	 *            the JDBC PreparedStatement to close.
	 */
	public static void release(PreparedStatement pst) {
		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e) {
				log.error("Could not close JDBC PrepareStatement", e);
			}
		}
	}

	/**
	 * Close the given JDBC Statement and ignore any thrown exception.
	 * 
	 * @param st
	 *            the JDBC Statement to close.
	 */
	public static void release(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				log.error("Could not close JDBC Statement", e);
			}
		}
	}

	/**
	 * Close the given JDBC ResultSet and ignore any thrown exception.
	 * 
	 * @param rs
	 *            the JDBC ResultSet to close.
	 */
	public static void release(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("Could not close ResultSet", e);
			}
		}
	}
}
