package org.xserver.component.core.http;

import org.xserver.component.core.Request;

/**
 * The HttpRequest basic operations
 * 
 * @author wangj
 * @since 2016/05/06
 *
 */
public interface HttpRequestAware extends Request {
	/**
	 * If the http request is post method return true, else return false
	 * 
	 * @return
	 */
	public boolean isPostMethod();

	/**
	 * Get the path of request's URI
	 * 
	 * @return
	 */
	public String getPath();

	/**
	 * Get the parameter by key(the http get method)
	 * 
	 * @param key
	 * @return
	 */
	public String getParameter(String key);

	/**
	 * Get the parameter by key(the http post method)
	 * 
	 * @param key
	 * @return
	 */
	public String getParameterByPost(String key);
}
