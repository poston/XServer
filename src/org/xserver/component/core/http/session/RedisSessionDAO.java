package org.xserver.component.core.http.session;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code RedisSessionDAO} extends {@code AbstractSessionDAO}, as a session
 * operation class, it use <b>redis</b> to CRUD sessions. Follow shiro
 * configuration, this class should be configure as sessionDAO that will take
 * over control session operations. <br>
 * The shiro configuration role:
 * 
 * <pre>
 * [main] 
 * # Objects and their properties are defined here, 
 * # Such as the securityManager, Realms and anything 
 * # else needed to build the SecurityManager
 * 
 * <strong>So we Configuration could like</strong></p>
 * <code>sessionDAO=org.xserver.component.shiro.RedisSessionDAO
 * securityManager.sessionManager.sessionDAO=$sessionDAO</code></p>
 * <strong>or use spring to inject</strong></p>
 * <code>&ltbean id="" class="" properties name="" ref=""&gt</code>
 * </pre>
 * 
 * @author postonzhang
 * @since 2015/04/24(This's my birthday :) )
 * 
 */
public class RedisSessionDAO extends AbstractSessionDAO {

	private RedisSessionDecoder redisSessionDecoder;

	private static final Logger log = LoggerFactory
			.getLogger(RedisSessionDAO.class);

	@Override
	public void update(Session session) {
		log.debug("update session:{} to redis", session);
		try {
			redisSessionDecoder.store(
					redisSessionDecoder.getRedisSessionPrefix()
							+ session.getId(), session);
		} catch (Exception e) {
			log.error("update session:{} error.", session, e);
		}
	}

	@Override
	public void delete(Session session) {
		log.debug("delete session:{} from redis", session);
		redisSessionDecoder.deleteSession(redisSessionDecoder
				.getRedisSessionPrefix() + session.getId());
	}

	@Override
	public Collection<Session> getActiveSessions() {
		log.debug("get active sessions");
		return redisSessionDecoder.getSessions();
	}

	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = generateSessionId(session);
		assignSessionId(session, sessionId);

		log.debug("create session:{} to redis", session);
		try {
			String key = redisSessionDecoder.getRedisSessionPrefix()
					+ session.getId();
			redisSessionDecoder.store(key, session);
		} catch (Exception e) {
			log.error("create session:{} error.", session, e);
		}
		return session.getId();
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		log.debug("read session from redis by sessionId:{}", sessionId);
		return redisSessionDecoder.getSession(sessionId);
	}

	public Collection<Session> getSessions() {
		log.debug("get all sessions from redis");
		return redisSessionDecoder.getSessions();
	}

	public RedisSessionDecoder getRedisSessionDecoder() {
		return redisSessionDecoder;
	}

	public void setRedisSessionDecoder(RedisSessionDecoder redisSessionDecoder) {
		this.redisSessionDecoder = redisSessionDecoder;
	}
}
