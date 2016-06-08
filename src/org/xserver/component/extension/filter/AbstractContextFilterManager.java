package org.xserver.component.extension.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.component.core.interfaces.InterfaceContext;
import org.xserver.component.core.interfaces.InterfaceMeta;
import org.xserver.component.exception.FilterProcessError;

/**
 * The {@code AbstractContextFilter} class loads basic filter informations, and
 * it contains XServer {@link InterfaceContext} supports extend class to use.
 * 
 * @author postonzhang
 * @since 2015/10/21
 * 
 */
public abstract class AbstractContextFilterManager implements ContextFilterManager {
	private static final Logger log = LoggerFactory.getLogger(AbstractContextFilterManager.class);

	private List<FilterConfig> filterChain;

	private Map<String, List<ContextFilter>> beforeFilterContext = new HashMap<String, List<ContextFilter>>();
	private Map<String, List<ContextFilter>> returnFilterContext = new HashMap<String, List<ContextFilter>>();

	public List<FilterConfig> getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(List<FilterConfig> filterChain) {
		this.filterChain = filterChain;
	}

	public Map<String, List<ContextFilter>> getBeforeFilterContext() {
		return beforeFilterContext;
	}

	public void setBeforeFilterContext(Map<String, List<ContextFilter>> beforeFilterContext) {
		this.beforeFilterContext = beforeFilterContext;
	}

	public Map<String, List<ContextFilter>> getReturnFilterContext() {
		return returnFilterContext;
	}

	public void setReturnFilterContext(Map<String, List<ContextFilter>> returnFilterContext) {
		this.returnFilterContext = returnFilterContext;
	}

	/**
	 * Load configured filters from <code>classpath:filterContext.xml</code>,
	 * this process will be invoked at the time of server start
	 */
	protected void initRequestFilterContext() {
		InterfaceContext interfaceContext = doGetInterfaceContext();

		Map<String, InterfaceMeta> interfaceMetas = interfaceContext.getInterfaceContext();

		Map<String, List<ContextFilter>> beforeFilterContext = new HashMap<String, List<ContextFilter>>(
				interfaceMetas.size());
		Map<String, List<ContextFilter>> returnFilterContext = new HashMap<String, List<ContextFilter>>(
				interfaceMetas.size());
		Map<String, Pattern> patterns = getPatternInfos();

		for (Entry<String, InterfaceMeta> entry : interfaceMetas.entrySet()) {
			String path = entry.getKey();

			log.info("initialize request path [{}]'s filters", path);

			List<ContextFilter> beforeFilters = new ArrayList<ContextFilter>();
			List<ContextFilter> returnFilters = new ArrayList<ContextFilter>();

			for (FilterConfig filterConfig : filterChain) {
				List<String> excludeRegexs = filterConfig.getExcludePaths();
				List<String> includeRegexs = filterConfig.getIncludePaths();

				if (matchRegex(excludeRegexs, path, patterns)) {
					continue;
				}

				if (matchRegex(includeRegexs, path, patterns)) {
					ContextFilter filter = filterConfig.getFilter();
					FilterType filterType = filter.getFilterType();

					if (filterType.equals(FilterType.BEFORE)) {
						beforeFilters.add(filter);
					}

					if (filterType.equals(FilterType.RETURN)) {
						returnFilters.add(filter);
					}
				}
			}

			beforeFilterContext.put(path, beforeFilters);
			returnFilterContext.put(path, returnFilters);
		}

		this.beforeFilterContext = beforeFilterContext;
		this.returnFilterContext = returnFilterContext;
	}

	/**
	 * Using pattern map to improve initialize request path filter chain.
	 * @return
	 */
	private Map<String, Pattern> getPatternInfos() {
		Map<String, Pattern> patterns = new HashMap<String, Pattern>();

		for (FilterConfig filterConfig : filterChain) {
			List<String> excludeRegexs = filterConfig.getExcludePaths();
			List<String> includeRegexs = filterConfig.getIncludePaths();

			if (excludeRegexs != null && !excludeRegexs.isEmpty()) {
				for (String regex : excludeRegexs) {
					Pattern pattern = Pattern.compile(regex);
					patterns.put(regex, pattern);
				}
			}

			if (includeRegexs != null && !includeRegexs.isEmpty()) {
				for (String regex : includeRegexs) {
					Pattern pattern = Pattern.compile(regex);
					patterns.put(regex, pattern);
				}
			}
		}

		return patterns;
	}

	/**
	 * subclass should implements this method, it get interface context
	 * @return
	 */
	protected abstract InterfaceContext doGetInterfaceContext();

	/**
	 * Check whether the path matches the list of regular expression, if matches
	 * return true else return false
	 * 
	 * @param excludeRegexs a list of exclude regular expression, like /A/.*
	 * @param key interface name, like /A/B
	 * @return if matches return true else return false
	 */
	private boolean matchRegex(List<String> excludeRegexs, String key, Map<String, Pattern> patterns) {
		if (excludeRegexs != null) {
			for (String regex : excludeRegexs) {
				if (patterns.get(regex).matcher(key).matches()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * initialize every filter in configured order
	 * 
	 * @throws Exception
	 */
	protected void contextInitialized() throws FilterProcessError {
		for (FilterConfig filterConfig : filterChain) {
			filterConfig.getFilter().contextInitialized();
		}
	}
}
