package org.xserver.component.extension.filter.parameter.util;

import org.xserver.component.core.XServerHttpRequest;

public interface ParameterValueGetter {
	public Object getValue(XServerHttpRequest request, Class<?> clazz,
			String key);
}
