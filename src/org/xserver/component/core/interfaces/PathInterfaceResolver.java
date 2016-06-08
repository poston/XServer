package org.xserver.component.core.interfaces;

import static org.xserver.component.core.interfaces.InterfaceContext.DEFAULT_INTERFACE_SPLIT;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.xserver.common.util.ReflectionUtil;
import org.xserver.component.annotation.Path;
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.handler.XServerHttpInterceptor;
import org.xserver.component.listener.XServerListener;
import org.xserver.component.log.StdOutLog;
import org.xserver.component.spring.SpringUtil;

/**
 * Load class method which with {@link Path} annotation as valid interfaces.
 * 
 * @author postonzhang
 * @since 2015/08/02
 * 
 */
@Component
public class PathInterfaceResolver implements InterfaceResolver {
	private static final Logger logger = LoggerFactory
			.getLogger(PathInterfaceResolver.class);
	private static final Logger stdOutLog = StdOutLog.getLogger();

	@Resource
	private XServerHttpConfig xServerHttpConfig;
	@Resource
	private XServerHttpInterceptor xServerHttpInterceptor;
	@Resource
	private XServerListener xServerListener;

	/**
	 * <h3>How to consider which class and method is a valid interfaces</h3>
	 * <ul>
	 * <li>the class and it's method must annotation with {@link Path}</li>
	 * <li>the method's arguments no longer limit</li>
	 * </ul>
	 * 
	 * @return path loaded interfaces
	 */
	@Override
	public Map<String, InterfaceMeta> interfaceContextResolver() {
		Map<String, InterfaceMeta> pathInterfaceContext = new HashMap<String, InterfaceMeta>();

		ApplicationContext ctx = SpringUtil.getApplicationContext();

		Map<String, Object> pathInterfaceMap = ctx
				.getBeansWithAnnotation(Path.class);

		for (Entry<String, Object> entry : pathInterfaceMap.entrySet()) {
			Path classPathAnnotation = entry.getValue().getClass()
					.getAnnotation(Path.class);
			String classPath = classPathAnnotation.value();

			for (Method method : ReflectionUtil.getMethodsWithAnnotation(entry
					.getValue().getClass(), Path.class)) {
				Path methodPathAnnotation = method.getAnnotation(Path.class);
				String methodPath = methodPathAnnotation.value();

				String key = DEFAULT_INTERFACE_SPLIT + classPath
						+ DEFAULT_INTERFACE_SPLIT + methodPath;
				
				InterfaceMeta interfaceMeta = new InterfaceMeta(entry
						.getValue().getClass(), method);
				pathInterfaceContext.put(key, interfaceMeta);
				xServerListener.putInterface(key);
				if (xServerHttpConfig.isInterceptor()) {
					xServerHttpInterceptor.putInterface(key);
				}
				xServerListener.putInterface(key);
				stdOutLog.info("Cache pathSolver interfaceMeta:{}",
						interfaceMeta);
				logger.info("Cache pathSolver interfaceMeta:{}", interfaceMeta);
			}
		}
		return pathInterfaceContext;
	}

	/**
	 * The interfaces key is the value of class's path annotation append
	 * {@code DEFAULT_INTERFACE_SPLIT} and append the value of method's path
	 * annotation
	 */
	@Override
	public String getInterfaceKey(InterfaceMeta interfaceMeta) {
		Path classPath = interfaceMeta.getClazz().getAnnotation(Path.class);
		Path methodPath = interfaceMeta.getMethod().getAnnotation(Path.class);

		return classPath.value() + DEFAULT_INTERFACE_SPLIT + methodPath.value();
	}

}
