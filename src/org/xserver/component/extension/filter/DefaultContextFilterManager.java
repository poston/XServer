package org.xserver.component.extension.filter;

import org.xserver.component.core.interfaces.InterfaceContext;

/**
 * Load server extended filters, configuration see
 * <code>classpath:extensionContext.xml</code>
 * 
 * @author postonzhang
 * @since 2015/11/03
 * 
 */
public class DefaultContextFilterManager extends AbstractContextFilterManager {
	private InterfaceContext interfaceContext;

	public InterfaceContext getInterfaceContext() {
		return interfaceContext;
	}

	public void setInterfaceContext(InterfaceContext interfaceContext) {
		this.interfaceContext = interfaceContext;
	}

	/**
	 * Initialize all filter initialization process and every request match
	 * filter chain.
	 */
	public void initFilterContext() throws Exception {
		contextInitialized();
		initRequestFilterContext();
	}

	@Override
	public InterfaceContext doGetInterfaceContext() {
		return getInterfaceContext();
	}
}
