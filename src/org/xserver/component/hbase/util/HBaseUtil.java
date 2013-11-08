package org.xserver.component.hbase.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.avro.generated.HBase;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.xserver.common.util.ReflectionUtil;
import org.xserver.component.annotation.Alias;
import org.xserver.component.annotation.ColumnFamily;
import org.xserver.component.hbase.exception.ClassTypeNotSupportException;

public class HBaseUtil {
	private static final Logger log = LoggerFactory.getLogger(HBase.class);
	private static Map<Class<?>, ReflectResultSet> simpleClass = initSimpleClass();

	private static Map<Class<?>, ReflectResultSet> initSimpleClass() {
		Map<Class<?>, ReflectResultSet> temp = new HashMap<Class<?>, ReflectResultSet>();

		ReflectResultSet rrs = new ReflectResultSet() {

			@Override
			public Object get(byte[] bytes) {
				return Bytes.toString(bytes);
			}

			@Override
			public byte[] get(Object value) {
				return Bytes.toBytes((String) value);
			}
		};
		temp.put(String.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(byte[] bytes) {
				return bytes;
			}

			@Override
			public byte[] get(Object value) {
				return new byte[] { (Byte) value };
			}

		};
		temp.put(Byte.class, rrs);
		temp.put(byte.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(byte[] bytes) {
				return Bytes.toShort(bytes);
			}

			@Override
			public byte[] get(Object value) {
				return Bytes.toBytes((Short) value);
			}
		};
		temp.put(Short.class, rrs);
		temp.put(short.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(byte[] bytes) {
				return Bytes.toBoolean(bytes);
			}

			@Override
			public byte[] get(Object value) {
				return Bytes.toBytes((Boolean) value);
			}

		};
		temp.put(Boolean.class, rrs);
		temp.put(boolean.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(byte[] bytes) {
				return Bytes.toInt(bytes);
			}

			@Override
			public byte[] get(Object value) {
				return Bytes.toBytes((Integer) value);
			}
		};
		temp.put(Integer.class, rrs);
		temp.put(int.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(byte[] bytes) {
				return Bytes.toLong(bytes);
			}

			@Override
			public byte[] get(Object value) {
				return Bytes.toBytes((Long) value);
			}

		};
		temp.put(Long.class, rrs);
		temp.put(long.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(byte[] bytes) {
				return Bytes.toFloat(bytes);
			}

			@Override
			public byte[] get(Object value) {
				return Bytes.toBytes((Float) value);
			}

		};
		temp.put(Float.class, rrs);
		temp.put(float.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(byte[] bytes) {
				return Bytes.toDouble(bytes);
			}

			@Override
			public byte[] get(Object value) {
				return Bytes.toBytes((Double) value);
			}
		};
		temp.put(Double.class, rrs);
		temp.put(double.class, rrs);

		rrs = new ReflectResultSet() {

			@Override
			public Object get(byte[] bytes) {
				return Bytes.toBigDecimal(bytes);
			}

			@Override
			public byte[] get(Object value) {
				return Bytes.toBytes((BigDecimal) value);
			}
		};
		temp.put(BigDecimal.class, rrs);
		temp.put(BigInteger.class, rrs);

		return temp;
	}

	public static <T> T reflectRow(List<KeyValue> list, Class<T> clazz)
			throws InstantiationException, IllegalAccessException {
		T t = clazz.newInstance();
		Map<String, Field> map = ReflectionUtil.getFieldMap(clazz);
		for (KeyValue kv : list) {
			String qualifier = Bytes.toString(kv.getQualifier());
			Field field = map.get(qualifier);
			if (field != null) {
				log.debug("qualifier: " + qualifier);
				field.setAccessible(true);
				ReflectResultSet rrs = simpleClass.get(field.getType());
				if (rrs == null) {
					throw new ClassTypeNotSupportException(
							"cannot reflect instance of [" + clazz.getName()
									+ "], because field type ["
									+ field.getType().getName()
									+ "] not support");
				}
				Object value = rrs.get(kv.getValue());
				field.set(t, value);
			}
		}

		return t;
	}

	public static Map<String, Map<String, Number>> reflectRow(
			List<KeyValue> list, Set<String> qualifiers) {
		Map<String, Map<String, Number>> result = new HashMap<String, Map<String, Number>>();
		Map<String, Number> row = new HashMap<String, Number>();

		String rowkey = Bytes.toString(list.get(0).getRow());
		for (KeyValue kv : list) {
			String qualifier = Bytes.toString(kv.getQualifier());
			if (qualifiers.contains(qualifier)) {
				Number number = null;
				try {
					number = Integer.parseInt(Bytes.toString(kv.getValue()));
				} catch (Exception e) {
					number = Double.parseDouble(Bytes.toString(kv.getValue()));
				}
				row.put(qualifier, number);
			}
		}

		result.put(rowkey, row);

		return result;
	}

	public static <T> void createPut(Put put, T t)
			throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = t.getClass().getDeclaredFields();
		for (Field field : fields) {
			ColumnFamily columnFamily = field.getAnnotation(ColumnFamily.class);
			if (columnFamily == null) {
				continue;
			}
			Alias alias = field.getAnnotation(Alias.class);
			String qualifier = alias == null ? field.getName() : alias.value();
			String family = columnFamily.value();
			ReflectResultSet rrs = simpleClass.get(field.getType());
			if (rrs == null) {
				throw new ClassTypeNotSupportException(
						"cannot put instance of [" + t.getClass().getName()
								+ " to hbase, because field type ["
								+ field.getType() + "] not support");
			}
			field.setAccessible(true);
			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),
					rrs.get(field.get(t)));
		}
	}

	/**
	 * HBase version 0.92.2 and greater version, we just close the table instead
	 * of putTable as the official document.
	 */
	public static void putTableOrClose(HTablePool pool, HTableInterface table) {
		String version = VersionInfo.getVersion();
		String[] versions = version.split("\\.");
		boolean close = true;
		if (Integer.parseInt(versions[0]) == 0
				|| Integer.parseInt(versions[1]) <= 92) {
			close = false;
		}

		if (close) {
			try {
				table.close();
			} catch (Exception e) {
				log.error("close table error", e);
			}
		} else {
			pool.putTable(table);
		}
	}

	public static void addProperties(Configuration configuration,
			Properties properties) {
		Assert.notNull(configuration, "A non-null configuration is required");
		if (properties != null) {
			Enumeration<?> props = properties.propertyNames();
			while (props.hasMoreElements()) {
				String key = props.nextElement().toString();
				configuration.set(key, properties.getProperty(key));
			}
		}
	}
}
