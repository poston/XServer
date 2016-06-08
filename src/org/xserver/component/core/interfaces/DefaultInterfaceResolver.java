package org.xserver.component.core.interfaces;

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
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.handler.XServerHttpInterceptor;
import org.xserver.component.listener.XServerListener;
import org.xserver.component.log.StdOutLog;
import org.xserver.component.spring.SpringUtil;
import org.xserver.wrap.WebInterface;

@Component
public class DefaultInterfaceResolver implements InterfaceResolver {
	private static final Logger logger = LoggerFactory
			.getLogger(DefaultInterfaceResolver.class);
	private static final Logger stdOutLog = StdOutLog.getLogger();

	@Resource
	private XServerHttpInterceptor xServerHttpInterceptor;
	@Resource
	private XServerListener xServerListener;
	@Resource
	private XServerHttpConfig xServerHttpConfig;

	/**
	 * <h3>How to consider which class and method is a valid interfaces</h3>
	 * <ul>
	 * <li>the class must implements {@link WebInterface}</li>
	 * <li>the class must annotation with {@link Component}, because the server
	 * depends on spring DI</li>
	 * <li>the method must with two arguments, one is {@link XServerHttpRequest}
	 * , another is {@link XServerHttpResponse}</li>
	 * </ul>
	 * 
	 * @return loaded default interfaces
	 */
	@Override
	public Map<String, InterfaceMeta> interfaceContextResolver() {
		Map<String, InterfaceMeta> defaultInterfaceContext = new HashMap<String, InterfaceMeta>();

		ApplicationContext ctx = SpringUtil.getApplicationContext();
		Map<String, WebInterface> interfaceMap = ctx
				.getBeansOfType(WebInterface.class);

		for (Entry<String, WebInterface> entry : interfaceMap.entrySet()) {
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
					String key = InterfaceContext.DEFAULT_INTERFACE_SPLIT + className + InterfaceContext.DEFAULT_INTERFACE_SPLIT
							+ method.getName();
					defaultInterfaceContext.put(key, interfaceMeta);
					if (xServerHttpConfig.isInterceptor()) {
						xServerHttpInterceptor.putInterface(key);
					}
					xServerListener.putInterface(key);

					stdOutLog.info("Cache defaultSolver interfaceMeta:{}",
							interfaceMeta);
					logger.info("Cache defaultSolver interfaceMeta:{}",
							interfaceMeta);
				}
			}
		}
		return defaultInterfaceContext;
	}

	/**
	 * The interfaces key is the value of class simple name(the first letter is
	 * lower case) append {@code DEFAULT_INTERFACE_SPLIT} and append method
	 * name.
	 */
	@Override
	public String getInterfaceKey(InterfaceMeta interfaceMeta) {
		return StringUtil.toLowerCaseAtIndex(interfaceMeta.getClazz()
				.getSimpleName(), 0)
				+ InterfaceContext.DEFAULT_INTERFACE_SPLIT + interfaceMeta.getMethod().getName();
	}
}
