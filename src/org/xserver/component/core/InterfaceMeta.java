package org.xserver.component.core;

import java.lang.reflect.Method;

import org.xserver.wrap.HttpInterface;

/**
 * The structure Meta data for HttpInterface
 * 
 * @author postonzhang
 * @since 2013/1/10
 */
public class InterfaceMeta {
	private Class<? extends HttpInterface> clazz;
	private Method method;

	public InterfaceMeta(Class<? extends HttpInterface> clazz, Method method) {
		this.clazz = clazz;
		this.method = method;
	}

	public Class<? extends HttpInterface> getClazz() {
		return clazz;
	}

	public void setClazz(Class<HttpInterface> clazz) {
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
