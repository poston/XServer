package org.xserver.component.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.xserver.common.util.StringUtil;
import org.xserver.component.handler.XServerHttpInterceptor;
import org.xserver.component.listener.XServerListener;
import org.xserver.component.log.StdOutLog;
import org.xserver.component.spring.SpringUtil;
import org.xserver.wrap.HttpInterface;

/**
 * <code>InterfaceContext</code> like the suffix word context that is the
 * interface cache. The key is SimpleClassName/methodName, and the value is
 * {@link InterfaceMeta} object.
 * 
 * @author postonzhang
 * @since 2013/2/22
 */
@Component
public class InterfaceContext {

	private static final Logger logger = LoggerFactory
			.getLogger(InterfaceContext.class);
	private static final Logger stdOutLog = StdOutLog.getLogger();

	private static Map<String, InterfaceMeta> interfaceContext;

	@Resource
	private XServerHttpInterceptor xServerHttpInterceptor;
	@Resource
	private XServerListener xServerListener;

	public static Map<String, InterfaceMeta> getInterfaceContext() {
		return interfaceContext;
	}

	public static void setInterfaceContext(
			Map<String, InterfaceMeta> interfaceContext) {
		InterfaceContext.interfaceContext = interfaceContext;
	}

	public Map<String, InterfaceMeta> loadInterfaceContext() {
		Map<String, InterfaceMeta> newInterfaceContext = new HashMap<String, InterfaceMeta>();

		ApplicationContext ctx = SpringUtil.getApplicationContext();
		Map<String, HttpInterface> interfaceMap = ctx
				.getBeansOfType(HttpInterface.class);

		for (Entry<String, HttpInterface> entry : interfaceMap.entrySet()) {
			String className = entry.getKey();

			for (Method method : entry.getValue().getClass().getMethods()) {
				Class<?>[] types = method.getParameterTypes();
				if (types.length != 2) {
					continue;
				}

				InterfaceMeta interfaceMeta = null;
				if (XServerHttpRequest.class.isAssignableFrom(types[0])
						&& XServerHttpResponse.class.isAssignableFrom(types[1])) {
					interfaceMeta = new InterfaceMeta(entry.getValue()
							.getClass(), method);
					String key = className + "/" + method.getName();
					newInterfaceContext.put(key, interfaceMeta);
					key = "/" + key;
					xServerHttpInterceptor.putInterface(key);
					xServerListener.putInterface(key);

					stdOutLog.info("Cache interfaceMeta:{}", interfaceMeta);
					logger.info("Cache interfaceMeta:{}", interfaceMeta);
				}
			}
		}

		interfaceContext = newInterfaceContext;

		return interfaceContext;
	}

	public HttpInterface getHttpInterface(String path) {
		if (StringUtil.isEmpty(path)) {
			throw new IllegalArgumentException(
					"URI's path should not be null or empty.");
		}

		ApplicationContext ctx = SpringUtil.getApplicationContext();
		String className = getClassName(path);
		return (HttpInterface) ctx.getBean(className);
	}

	public InterfaceMeta getInterfaceMeta(String path) {
		String classNameAndmethodName = path;
		if (path.startsWith("/")) {
			classNameAndmethodName = path.substring(1);
		}

		return interfaceContext.get(classNameAndmethodName);
	}

	private String getClassName(String path) {
		assert path != null && path.length() != 0;
		String className = "";
		if (path.startsWith("/")) {
			className = path.substring(1, path.indexOf("/", 1));
		} else {
			className = path.substring(0, path.indexOf("/"));
		}

		return className;
	}
}
