package org.xserver.component.core.http.session;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.xserver.component.core.http.HttpRequestCookieAware;

/**
 * 
 * @author postonzhang
 * @since 2015/08/01
 *
 */
public interface SessionDecoder {

	public String getSessionName();

	public void setSessionName(String sessionName);

	public int getExpireTimeInSeconds();

	public void setExpireTimeInSeconds(int expireTimeInSeconds);

	public boolean isValid(String sessionId);

	/**
	 * store session
	 * 
	 * @param sessionId
	 * @param session
	 */
	public void store(Serializable sessionId, Session session) throws Exception;

	/**
	 * Renew session with sessionId, if the session not found do nothing, else
	 * renew the session with expireTimeInSeconds
	 * 
	 * @param sessionId
	 */
	public void renewSession(Serializable sessionId) throws Exception;

	/**
	 * Delete session instance
	 * 
	 * @param sessionId
	 */
	public void deleteSession(Serializable sessionId) throws Exception;

	/**
	 * Get session instance
	 * 
	 * @param sessionId
	 * @return
	 */
	public Session getSession(Serializable sessionId) throws Exception;

	/**
	 * Get all valid sessions
	 * 
	 * @return the set of sessions
	 */
	public Collection<Session> getSessions() throws Exception;

	/**
	 * get the return value if exists the sessionId
	 * 
	 * @param sessionId
	 * @return
	 */
	public boolean exists(String sessionId);

	/**
	 * Try to get sessionId from request
	 * 
	 * @param request
	 *            current request from client
	 * @return sessionId
	 */
	public String resolveSessionId(HttpRequestCookieAware request);
}
