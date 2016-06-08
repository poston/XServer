package org.xserver.component.core.http.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.component.core.http.HttpRequestCookieAware;
import org.xserver.component.extension.shiro.util.SerializeUtils;
import org.xserver.component.json.JsonManager;
import org.xserver.component.redis.RedisTemplate;

/**
 * RedisSessionManager use <strong>redis</strong> to store, get, delete and
 * update sessions
 * 
 * @author postonzhang
 * @since 2015/04/29
 * 
 */
public class RedisSessionDecoder implements SessionDecoder {

	private static final Logger log = LoggerFactory
			.getLogger(RedisSessionDecoder.class);

	private int expireTimeInSeconds;

	private String sessionName;

	private RedisTemplate redisTemplate;

	private static final String DEFAULT_REDIS_SESSION_PREFIX = "redis_session:";

	private static final int DEFAULT_REDIS_SESSION_EXPIRE_TIME_IN_SECONDS = 30 * 60;

	private static final String DEFAULT_SESSION_NAME = "jsessionid";

	private static final String WILDCARD = "*";

	private String redisSessionPrefix;

	public RedisSessionDecoder() {
		this(null);
	}

	public RedisSessionDecoder(RedisTemplate redisTemplate) {
		this(redisTemplate, DEFAULT_REDIS_SESSION_PREFIX);
	}

	public RedisSessionDecoder(RedisTemplate redisTemplate,
			String redisSessionPrefix) {
		this(redisTemplate, redisSessionPrefix,
				DEFAULT_REDIS_SESSION_EXPIRE_TIME_IN_SECONDS);
	}

	public RedisSessionDecoder(RedisTemplate redisTemplate,
			String redisSessionPrefix, int expireTimeInSeconds) {
		this(redisTemplate, redisSessionPrefix, expireTimeInSeconds,
				DEFAULT_SESSION_NAME);
	}

	public RedisSessionDecoder(RedisTemplate redisTemplate,
			String redisSessionPrefix, int expireTimeInSeconds,
			String sessionName) {
		this.redisTemplate = redisTemplate;
		this.redisSessionPrefix = redisSessionPrefix;
		this.expireTimeInSeconds = expireTimeInSeconds;
		this.sessionName = sessionName;
	}

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public String getRedisSessionPrefix() {
		return redisSessionPrefix;
	}

	public void setRedisSessionPrefix(String redisSessionPrefix) {
		this.redisSessionPrefix = redisSessionPrefix;
	}

	@Override
	public String getSessionName() {
		if (sessionName == null || "".endsWith(sessionName)) {
			sessionName = DEFAULT_SESSION_NAME;
		}
		return sessionName;
	}

	@Override
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	@Override
	public int getExpireTimeInSeconds() {
		return expireTimeInSeconds;
	}

	@Override
	public void setExpireTimeInSeconds(int expireTimeInSeconds) {
		this.expireTimeInSeconds = expireTimeInSeconds;
	}

	@Override
	public boolean isValid(String sessionId) {
		return exists(sessionId);
	}

	/**
	 * store session, the parameter sessionId without the REDIS_SESSION_PREFIX
	 * 
	 * @param sessionId
	 * @param session
	 */
	public void store(Serializable sessionId, Session session) throws Exception {
		log.debug("store session:{} with sessionId:{}", new Object[] {
				sessionId, session });
		redisTemplate.setKeyValueExpire(sessionId.toString().getBytes(),
				SerializeUtils.serialize(session), expireTimeInSeconds);
	}

	/**
	 * Renew session with sessionId, if the session not found do nothing, else
	 * renew the session with expireTimeInSeconds, the parameter sessionId
	 * without the redisSessionPrefix
	 * 
	 * @param sessionId
	 */
	public void renewSession(Serializable sessionId) throws Exception {
		log.debug("renew session, the sessionId is {}", sessionId);
		if (!redisTemplate.exists(prefixSession(sessionId))) {
			log.debug("not found session by sessionId:{}", sessionId);
			throw new RuntimeException("not found session by sessionId["
					+ sessionId + "]");
		}

		redisTemplate.expire(prefixSession(sessionId), expireTimeInSeconds);
	}

	/**
	 * if exists the sessionId return true else return false
	 * 
	 * @param sessionId
	 */
	public boolean exists(String sessionId) {
		return redisTemplate.exists(prefixSession(sessionId));
	}

	/**
	 * Delete session instance from redis, the parameter sessionId without the
	 * redisSessionPrefix
	 * 
	 * @param sessionId
	 */
	public void deleteSession(Serializable sessionId) {
		log.debug("delete session by sessionId:{}", sessionId);
		redisTemplate.delete(prefixSession(sessionId));
	}

	/**
	 * Get session instance from redis, the parameter sessionId without the
	 * redisSessionPrefix
	 * 
	 * @param sessionId
	 * @return
	 */
	public Session getSession(Serializable sessionId) {
		log.debug("get session, the sessionId is {}", sessionId);

		return (Session) SerializeUtils.deserialize(redisTemplate
				.getValue((redisSessionPrefix + sessionId.toString())
						.getBytes()));
	}

	/**
	 * Get session instance from redis, the parameter sessionId with the
	 * redisSessionPrefix
	 * 
	 * @param sessionId
	 * @return
	 */
	public Session getSession0(Serializable sessionId) {
		log.debug("get session, the sessionId is {}", sessionId);

		return (Session) SerializeUtils.deserialize(redisTemplate
				.getValue(sessionId.toString().getBytes()));
	}

	/**
	 * Get all valid sessions from redis
	 * 
	 * @return the set of sessions
	 */
	public Collection<Session> getSessions() {
		log.debug("get all sessions from redis");
		Set<String> keys = redisTemplate.keys(redisSessionPrefix + WILDCARD);
		Set<Session> sessions = new HashSet<Session>(keys.size());
		for (String key : keys) {
			sessions.add(getSession0(key));
		}

		return sessions;
	}

	/**
	 * Prefix session with {@code redisSessionPrefix}
	 * 
	 * @param sessionId
	 * @return
	 */
	private String prefixSession(Serializable sessionId) {
		return redisSessionPrefix + sessionId.toString();
	}

	/**
	 * Try to get sessionId from request, first we try to get value from cookie,
	 * if cannot be found, try looking value from URL's parameter.
	 * 
	 * @param request
	 *            current request from client
	 * @return sessionId
	 */
	@Override
	public String resolveSessionId(HttpRequestCookieAware request) {
		// if cookie enabled, try to get sessionId from cookie
		String sessionId = request.getCookie(this.getSessionName());

		// if cannot get, try looking for sessionId from parameters
		if (sessionId == null) {
			sessionId = request.getParameter(this.getSessionName());
		}

		/**
		 * Whether need to look for sessionId from URI's redirect parameter?
		 * Like http://host:port/path;jsessionid=x?param1=x&param2=x. If need to
		 * do this, should add this logic process, like as:
		 * <code>request.getRedirectParameter(sessionManager.getSessionName())</code>
		 */

		return sessionId;
	}

	public static void main(String[] args) {
		String json = "{\"id\":\"1dc660c8-93fb-4ff5-85c5-929281eed78f\",\"valid\":true,\"lastAccessTime\":1440556468300,\"startTimestamp\":1440556468300,\"attributeKeys\":[],\"timeout\":1800000,\"expired\":false}";
		SimpleSession ses = JsonManager.getBean(json, SimpleSession.class);
		System.out.println(ses);
	}
}
