package org.xserver.component.core;

import java.util.Collection;

import org.apache.shiro.session.Session;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.component.core.http.NettyHttpRequest;
import org.xserver.component.core.http.session.SessionAware;
import org.xserver.component.core.http.session.SessionDecoder;
import org.xserver.component.core.http.session.exception.InvalidSessionException;
import org.xserver.component.spring.SpringUtil;

/**
 * Wrap HttpRequest, Cookie, Session provider <code>getParamterXXX</code> and
 * <code>getParameterXXXByPost</code> method to gain request parameter(s)
 * 
 * @author postonzhang
 * @since 2013/01/10
 * 
 */
public class XServerHttpRequest extends NettyHttpRequest implements
		SessionAware {

	private static final Logger log = LoggerFactory
			.getLogger(XServerHttpRequest.class);

	private Session session;
	private SessionDecoder sessionDecoder;

	public XServerHttpRequest(HttpVersion httpVersion, HttpMethod method,
			String uri) {
		super(httpVersion, method, uri);
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void setSessionDecoder(SessionDecoder sessionDecoder) {
		this.sessionDecoder = sessionDecoder;
	}

	@Override
	public Session getSession() {
		if (session == null) {
			SessionDecoder sessionDecoder = getSessionDecoder();
			String sessionId = sessionDecoder.resolveSessionId(this);
			try {
				session = sessionDecoder.getSession(sessionId);
			} catch (Exception e) {
				log.error("get session error", e);
			}
		}

		return session;
	}

	/**
	 * Get the session manager
	 * 
	 * @return the queryStringDecoder
	 */
	private SessionDecoder getSessionDecoder() {
		if (sessionDecoder == null) {
			sessionDecoder = (SessionDecoder) SpringUtil
					.getBean(SessionDecoder.class);
		}

		return sessionDecoder;
	}

	@Override
	public String getSessionId() {
		return getSession().getId().toString();
	}

	@Override
	public Collection<Object> getAttributeKeys() throws InvalidSessionException {
		if (getSession() != null) {
			return getSession().getAttributeKeys();
		}

		return null;
	}

	@Override
	public Object getAttribute(Object key) throws InvalidSessionException {
		if (getSession() != null) {
			return getSession().getAttribute(key);
		}

		return null;
	}

}
