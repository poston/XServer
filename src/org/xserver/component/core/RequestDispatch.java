package org.xserver.component.core;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.common.util.StringUtil;
import org.xserver.component.exception.ResouceNotFoundError;
import org.xserver.component.handler.ext.AbstractWriteHandler;
import org.xserver.component.handler.ext.ThrowableHandler;
import org.xserver.component.handler.ext.WriteHandlerManager;
import org.xserver.component.spring.SpringUtil;
import org.xserver.wrap.HttpInterface;

/**
 * <h3>Request dispatch</h3>
 * 
 * The real business logic httpInterface method is mapped from the
 * {@link InterfaceContext} , which is invoked to work and then send the wrote
 * response to next downstream in pipeline. Always invoke httpInterface method
 * return Object, which should write its content by {@link AbstractWriteHandler}
 * .
 * 
 * @author postonzhang
 * @since 2013/2/26
 */
@Component
public class RequestDispatch {
	private static final Logger logger = LoggerFactory
			.getLogger(RequestDispatch.class);
	@Resource
	private InterfaceContext interfaceContext;
	@Resource
	private ThrowableHandler throwableHandler;
	@Resource
	private WriteHandlerManager writeHandlerManager;

	public void requestDispatch(XServerHttpContextAttachment attachment) {
		XServerHttpRequest request = attachment.getRequest();

		String path = request.getPath();
		Object interfaceReturn = null;
		try {
			interfaceReturn = interfaceInvoke(path, attachment);
			writeHandlerManager.writeResponse(attachment, interfaceReturn);
		} catch (Throwable e) {
			/**
			 * There should not be Exception, we use Error for interrupt the
			 * workflow.
			 */
			throwableHandler.handleThrowable(attachment, e);
			logger.error("Request [" + request.getUri() + "] invoke fail.", e);
		}
	}

	private Object interfaceInvoke(String path,
			XServerHttpContextAttachment attachment) throws Exception {
		InterfaceMeta interfaceMeta = interfaceContext.getInterfaceMeta(path);
		if (interfaceMeta == null) {
			throw new ResouceNotFoundError(HttpResponseStatus.BAD_REQUEST,
					"PATH \'" + path + "\' CANNOT MATCH ANY INTERFACE.");
		}

		Method method = interfaceMeta.getMethod();
		Class<? extends HttpInterface> clazz = interfaceMeta.getClazz();
		HttpInterface httpInterface = (HttpInterface) SpringUtil
				.getBean(StringUtil.toLowerCaseAtIndex(clazz.getSimpleName(), 0));
		return method.invoke(httpInterface, attachment.getRequest(),
				attachment.getResponse());
	}
}
