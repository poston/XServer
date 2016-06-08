package org.xserver.component.core.interfaces;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.xserver.common.util.StringUtil;
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.handler.XServerHttpInterceptor;
import org.xserver.component.listener.XServerListener;
import org.xserver.component.spring.SpringUtil;
import org.xserver.wrap.WebInterface;

/**
 * <code>InterfaceContext</code> like the suffix word context that is the
 * interfaces cache. The key is made by SimpleClassName and methodName or
 * something else, and the value is {@link InterfaceMeta} object.
 * 
 * <h3>Event timeline</h3>
 * <ul>
 * <li>2013/02/22 support default web interfaces, see
 * {@link InterfaceContext#defaultInterfaceContextResolver()}</li>
 * <li>2015/08/02 support Annotation web interfaces, see
 * {@link #pathInterfaceContextResolver()}</li>
 * </ul>
 * 
 * @author postonzhang
 * @since 2013/2/22
 */
@Component
public class InterfaceContext {
	private Map<String, InterfaceMeta> interfaceContext;

	@Resource
	private XServerHttpInterceptor xServerHttpInterceptor;
	@Resource
	private XServerListener xServerListener;
	@Resource
	private XServerHttpConfig xServerHttpConfig;

	@Resource
	private InterfaceResolverManager interfaceResolverManager;

	public static final String DEFAULT_INTERFACE_SPLIT = "/";

	public Map<String, InterfaceMeta> getInterfaceContext() {
		return interfaceContext;
	}

	public void setInterfaceContext(Map<String, InterfaceMeta> interfaceContext) {
		this.interfaceContext = interfaceContext;
	}

	public Map<String, InterfaceMeta> loadInterfaceContext() {
		interfaceContext = interfaceResolverManager.getInterfaceMetaResolver();
		return interfaceContext;
	}

	/**
	 * Get the request represented WebInterface class
	 * 
	 * @param path
	 * @return
	 */
	public WebInterface getHttpInterface(String path) {
		if (StringUtil.isEmpty(path)) {
			throw new IllegalArgumentException(
					"URI's path should not be null or empty.");
		}

		ApplicationContext ctx = SpringUtil.getApplicationContext();
		String className = getClassName(path);
		return (WebInterface) ctx.getBean(className);
	}

	public InterfaceMeta getInterfaceMeta(String path) {
		return interfaceContext.get(path);
	}

	private String getClassName(String path) {
		assert path != null && path.length() != 0;
		String className = path.startsWith(DEFAULT_INTERFACE_SPLIT) ? path
				.substring(1, path.indexOf(DEFAULT_INTERFACE_SPLIT, 1)) : path
				.substring(0, path.indexOf(DEFAULT_INTERFACE_SPLIT));

		return className;
	}
}
