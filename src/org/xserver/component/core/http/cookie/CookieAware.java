package org.xserver.component.core.http.cookie;

import java.util.Map;

import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;

public interface CookieAware {
	/**
	 * Get the request Decoder, if this instance not null just return, else new
	 * and return
	 * 
	 * @return the cookieDecoder
	 */
	public CookieDecoder getCookieDecoder();
	
	/**
	 * Get all client cookie, return map
	 * 
	 * @return cookie key-value map
	 */
	public Map<String, String> getCookieMap();
	
	/**
	 * Get the cookie by provided key
	 * 
	 * @param key
	 *            provided key
	 * @return the cookie's value
	 */
	public String getCookie(String key);
	
	/**
	 * Get all client cookies
	 * 
	 * @return cookie array
	 */
	public Cookie[] getCookies();
}
