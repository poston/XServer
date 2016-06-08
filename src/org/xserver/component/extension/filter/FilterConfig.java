package org.xserver.component.extension.filter;

import java.util.List;

/**
 * The filter description, include filter name, filter class, and filter match
 * conditions.
 * 
 * @author postonzhang
 * @since 2015/10/22
 * 
 */
public class FilterConfig {
	/**
	 * Filter instance
	 */
	private ContextFilter filter;
	/**
	 * A set of filter's exclude path conditions
	 */
	private List<String> excludePaths;

	/**
	 * A set of filter's include path conditions
	 */
	private List<String> includePaths;

	public ContextFilter getFilter() {
		return filter;
	}

	public void setFilter(ContextFilter filter) {
		this.filter = filter;
	}

	public List<String> getExcludePaths() {
		return excludePaths;
	}

	public void setExcludePaths(List<String> excludePaths) {
		this.excludePaths = excludePaths;
	}

	public List<String> getIncludePaths() {
		return includePaths;
	}

	public void setIncludePaths(List<String> includePaths) {
		this.includePaths = includePaths;
	}

	@Override
	public String toString() {
		return "FilterConfig [filter=" + filter + ", excludePaths=" + excludePaths + ", includePaths=" + includePaths
				+ "]";
	}
}
