package org.xserver.component.hbase.util;

public interface ReflectResultSet {
	public Object get(byte[] bytes);
	public byte[] get(Object value);
}
