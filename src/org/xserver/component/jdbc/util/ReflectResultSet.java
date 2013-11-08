package org.xserver.component.jdbc.util;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ReflectResultSet {
	public Object get(ResultSet rs, Field field) throws SQLException;
}
