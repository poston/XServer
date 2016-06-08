package org.xserver.component.extension.filter;

import java.util.List;

/**
 * Manage all filter, support filter context
 * 
 * @author postonzhang
 * @since 2016/06/08
 *
 */
public interface ContextFilterManager {

	/**
	 * get the filter chain, return all filter in order
	 * @return all filter configured
	 */
	public List<FilterConfig> getFilterChain();
}
