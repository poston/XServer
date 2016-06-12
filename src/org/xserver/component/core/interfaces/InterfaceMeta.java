package org.xserver.component.core.interfaces;

import java.lang.reflect.Method;

/**
 * The structure Meta data for HttpInterface
 * 
 * @author postonzhang
 * @since 2013/1/10
 */
public class InterfaceMeta {
	private Class<?> clazz;
	private Method method;

	public InterfaceMeta(Class<?> clazz, Method method) {
		this.clazz = clazz;
		this.method = method;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	@Override
	public String toString() {
		return "<" + clazz.getName() + ", " + method.getName() + ">";
	}
}
