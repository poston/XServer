package org.xserver.component.extension.shiro.filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.xserver.common.util.StringUtil;
import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.core.http.session.SessionDecoder;
import org.xserver.component.exception.FilterProcessError;
import org.xserver.component.extension.filter.AbstractContextFilter;
import org.xserver.component.extension.shiro.exception.SessionInvalidError;
import org.xserver.component.extension.shiro.exception.SessionLoginError;
import org.xserver.component.extension.shiro.exception.SessionNullError;
import org.xserver.component.extension.shiro.exception.ShiroRealmCheckError;
import org.xserver.component.handler.ext.WriteHandlerManager;
import org.xserver.component.handler.ext.WriteHandlerManager.WriteType;
import org.xserver.component.spring.SpringUtil;

/**
 * The {@code XServerShiroHandler} aim at checking whether the request is valid
 * or not, and decided to renew session. After this check and renew operation,
 * event will be send to next handler(mostly is {@code RequestDispatchHandler})
 * 
 * <pre>
 * Logic Process:
 * <ul>
 * <li>loginURL: call {@link Subject#login(org.apache.shiro.authc.AuthenticationToken)}, if return true, we response client login success</li>
 * <li>logoutURL: call {@link Subject#logout()} and cleanXXX</li>
 * <li>otherURL: first session validation, false session expire or is invalid, else to call {@link Subject#isAuthenticated()}, if return true and check permission, else clean cookie</li>
 * </ul>
 * </pre>
 * 
 * @author postonzhang
 * @since 2015/05/01
 * 
 */
public class XServerShiroFilter extends AbstractContextFilter {
	private static final Logger log = LoggerFactory.getLogger(XServerShiroFilter.class);

	private static final String DEFAULT_PATH_LOGIN_URL = "/login/login";
	private static final String DEFAULT_PATH_LOGOUT_URL = "/login/logout";

	public static final String DEFAULT_PATH_LOGIN_HTML = "login.html";
	public static final String DEFAULT_PATH_INDEX_HTML = "index.html";

	private static final String DEFAULT_COOKIE_PATH = "/";
	private static final String DEFAULT_COOKIE_BLANK_VALUE = "";

	private static final int DEFAULT_COOKIE_INVALID_MAXAGE = 0;
	private static final int DEFAULT_COOKIE_MAXAGE = -1;

	private static final String DEFAULT_INPUT_TEXT_USERNAME = "username";
	private static final String DEFAULT_INPUT_TEXT_PASSWORD = "password";

	private static final int ERROR_LOGIN_ERROE = 401;
	private static final int ERROR_LOGOUT = 302;
	private static final int ERROR_SESSION_EXPIRE = 503;
	private static final int ERROR_NO_PERMISSION = 403;

	public static final String ATTRIBUTE_PRINCIPAL = "principal";

	/* LOGIN URL */
	private String loginURL;
	/* LOGOUT URL */
	private String logoutURL;
	/* DOMAIN VALUE */
	private String domain;
	/* MAX AGE */
	private int maxAge;

	private SessionDecoder sessionManager;

	public XServerShiroFilter(SessionDecoder sessionManager) {
		this(DEFAULT_PATH_LOGIN_URL, DEFAULT_PATH_LOGOUT_URL, sessionManager);
	}

	public XServerShiroFilter(String loginURL, String logoutURL, SessionDecoder sessionManager) {
		this.loginURL = loginURL;
		this.logoutURL = logoutURL;
		this.maxAge = DEFAULT_COOKIE_MAXAGE;
		this.sessionManager = sessionManager;
	}

	public XServerShiroFilter(String loginURL, String logoutURL, String domain, SessionDecoder sessionManager) {
		this(loginURL, logoutURL, domain, DEFAULT_COOKIE_MAXAGE, sessionManager);
	}

	public XServerShiroFilter(String loginURL, String logoutURL, String domain, int maxAge,
			SessionDecoder sessionManager) {
		this.loginURL = loginURL;
		this.logoutURL = logoutURL;
		this.domain = domain;
		this.maxAge = maxAge;
		this.sessionManager = sessionManager;
	}

	public String getLoginURL() {
		return loginURL;
	}

	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}

	public String getLogoutURL() {
		return logoutURL;
	}

	public void setLogoutURL(String logoutURL) {
		this.logoutURL = logoutURL;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	@Override
	public void process(XServerHttpContextAttachment attachment) throws FilterProcessError {
		sessionFilter(attachment);
	}

	/**
	 * we make sessionValidationSchedulerEnabled false in configuration.
	 * 
	 * <ul>
	 * Filter flow:
	 * <li><strong>login</strong>: use shiro to check <strong>username</strong>
	 * and <strong>password</strong>, and generate session and cookie</li>
	 * <li><strong>logout</strong>: clear session and cookie</li>
	 * <li><strong>normal request with valid session</strong>: check role</li>
	 * <li><strong>normal request with invalid session</strong>: redirect to
	 * login html</li>
	 * </ul>
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void sessionFilter(XServerHttpContextAttachment attachment) throws FilterProcessError {
		/*
		 * if the path need to check permission, system will check and renew
		 * session, so we must configure which paths need check
		 */
		String path = attachment.getRequest().getPath();

		try {
			if (path.equals(loginURL)) {
				login(attachment);
			} else if (path.equals(logoutURL)) {
				logout(attachment);
			} else {
				realmCheck(attachment);
			}
		} catch (Exception e) {
			throw new FilterProcessError(this.getClass().getSimpleName(), e.getMessage());
		}
	}

	private void login(XServerHttpContextAttachment attachment) {
		buildSubject(attachment);
	}

	private void buildSubject(XServerHttpContextAttachment attachment) {
		// TODO use POST http method
		String username = attachment.getRequest().getParameter(DEFAULT_INPUT_TEXT_USERNAME);
		String password = attachment.getRequest().getParameter(DEFAULT_INPUT_TEXT_PASSWORD);

		Subject subject = SecurityUtils.getSubject();

		UsernamePasswordToken token = new UsernamePasswordToken(username, password);

		try {
			subject.login(token);
		} catch (Exception e) {
			log.error("login occur error, the username[" + username + "]", e);

			writeResponse(attachment, ERROR_LOGIN_ERROE);

			throw new SessionLoginError(username, e.getMessage());
		}

		Session session = subject.getSession();
		session.setAttribute(ATTRIBUTE_PRINCIPAL, subject.getPrincipal());
		String sessionId = session.getId().toString();

		Cookie cookie = new DefaultCookie(sessionManager.getSessionName(), sessionId);
		cookie.setHttpOnly(true);
		cookie.setPath(DEFAULT_COOKIE_PATH);
		if (!StringUtil.isEmpty(domain)) {
			cookie.setDomain(domain);
		}
		/*
		 * if i set cookie maxage = -1, the following request will not take the
		 * cookie.
		 */
		// cookie.setMaxAge(maxAge);

		attachment.getResponse().addCookie(cookie);
	}

	private void logout(XServerHttpContextAttachment attachment) {
		destorySubject(attachment);
	}

	private void destorySubject(XServerHttpContextAttachment attachment) {
		String sessionId = sessionManager.resolveSessionId(attachment.getRequest());
		log.info("Logout the subject specified by sessionId[" + sessionId + "].");

		try {
			cleanSubject(sessionId);
		} finally {
			try {
				cleanSession(sessionId);
			} finally {
				cleanCookie(attachment);
			}
		}

		// redirect to the login page.
		writeResponse(attachment, ERROR_LOGOUT);
		throw new FilterProcessError(this.getClass().getName(), "logout");
	}

	private void realmCheck(XServerHttpContextAttachment attachment) {
		String path = attachment.getRequest().getPath();

		Assert.isTrue(!path.equals(loginURL) && !path.equals(logoutURL), "The path[" + path
				+ "] is invalid, it should not go here");

		String sessionId = sessionManager.resolveSessionId(attachment.getRequest());

		if (StringUtil.isEmpty(sessionId)) {
			cleanCookie(attachment);

			writeResponse(attachment, ERROR_SESSION_EXPIRE);

			throw new SessionNullError("session should not be null");
		}

		if (!sessionManager.isValid(sessionId)) {
			try {
				cleanSession(sessionId);
			} finally {
				cleanCookie(attachment);
			}

			writeResponse(attachment, ERROR_SESSION_EXPIRE);

			throw new SessionInvalidError(sessionId, "there no session, session maybe expire or session is invalid");
		}

		Subject subject = new Subject.Builder().sessionId(sessionId).buildSubject();
		boolean auth = subject.isAuthenticated();

		// if the subject is authenticated, we should check the permission, if
		// pass the session should be renew
		if (auth) {
			boolean permitted = subject.isPermitted(path);

			// TODO UserRealm
			permitted = true;

			if (permitted) {
				try {
					sessionManager.renewSession(sessionId);
				} catch (Exception e) {
					log.error("renew session[" + sessionId + "] error", e);
					throw new ShiroRealmCheckError(sessionId, "renew session error");
				}
			} else {
				log.error("this path[" + path + "] is not permitted for the session[" + sessionId + "].");

				writeResponse(attachment, ERROR_NO_PERMISSION);

				throw new ShiroRealmCheckError(sessionId, "the path[" + path + "] is not permitted for the session["
						+ sessionId + "]");
			}
		} else {
			writeResponse(attachment, ERROR_SESSION_EXPIRE);

			throw new SessionInvalidError(sessionId, "authenticate the session[" + sessionId + "] is false.");
		}
	}

	private void cleanSubject(String sessionId) {
		try {
			/**
			 * If session is valid, then we do logout logic, else nothing. Note
			 * if the session is already expire, the build subject cannot find
			 * subject, but this process
			 * <code>new Subject.Builder().sessionId(sessionId).buildSubject()</code>
			 * will not throw exception, so we cannot catch the method
			 */
			if (!StringUtil.isEmpty(sessionId) && sessionManager.exists(sessionId)) {
				Subject subject = new Subject.Builder().sessionId(sessionId).buildSubject();
				subject.logout();
			}
		} catch (Exception e) {
			log.error("logout error, the user's session[" + sessionId + "]", e);
			throw new FilterProcessError(this.getClass().getName(), "logout error");
		}
	}

	private void cleanSession(String sessionId) {
		if (!StringUtil.isEmpty(sessionId)) {
			try {
				sessionManager.deleteSession(sessionId);
			} catch (Exception e) {
				log.error("delete session[" + sessionId + "] error", e);
				throw new FilterProcessError(this.getClass().getName(), "delete session error");
			}
		}
	}

	/**
	 * clear cookie while logout the system
	 * 
	 * @param attachment
	 */
	private void cleanCookie(XServerHttpContextAttachment attachment) {
		Cookie[] cookies = attachment.getRequest().getCookies();
		for (Cookie cookie : cookies) {
			Cookie cookienew = new DefaultCookie(cookie.getName(), DEFAULT_COOKIE_BLANK_VALUE);
			cookienew.setMaxAge(DEFAULT_COOKIE_INVALID_MAXAGE);
			cookienew.setPath(DEFAULT_COOKIE_PATH);
			attachment.getResponse().addCookie(cookienew);
		}
	}
}
