package org.xserver.component.core.interfaces;

import java.util.Map;

/**
 * The interfaces will mount valid interfaces to server, it's delegated to
 * subclass that whether interfaces are valid.
 * 
 * @author postonzhang
 * @since 2015/10/31(this is Saturday, 0_o.zZ)
 * 
 */
public interface InterfaceResolver {
	/**
	 * Scan class method, and depend on subclass implemented resolver to check
	 * which is valid interfaces.
	 * 
	 * @return valid interfaces group
	 */
	public Map<String, InterfaceMeta> interfaceContextResolver();

	/**
	 * Get interfaces key, that is the interfaces identification.
	 * 
	 * @return interfaces identification
	 */
	public String getInterfaceKey(InterfaceMeta interfaceMeta);
}
