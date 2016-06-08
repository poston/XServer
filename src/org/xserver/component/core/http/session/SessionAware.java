package org.xserver.component.core.http.session;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.xserver.component.core.http.session.exception.InvalidSessionException;

/**
 * The <code>SessionAware</code> class is aiming at support session
 * 
 * @author postonzhang
 * @since 2016/05/07
 */
public interface SessionAware {

	/**
	 * get session instance
	 * @return
	 * @throws InvalidSessionException
	 */
	public Session getSession() throws InvalidSessionException;

	/**
	 * get session instance id
	 * @return
	 * @throws InvalidSessionException
	 */
	public Serializable getSessionId() throws InvalidSessionException;

	/**
	 * get all attribute keys
	 * @return
	 * @throws InvalidSessionException
	 */
	public Collection<Object> getAttributeKeys() throws InvalidSessionException;

	/**
	 * get the session attribute by key
	 * @param key
	 * @return
	 * @throws InvalidSessionException
	 */
	public Object getAttribute(Object key) throws InvalidSessionException;
}
