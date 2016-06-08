package org.xserver.component.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.common.util.StringUtil;
import org.xserver.component.core.interfaces.InterfaceContext;
import org.xserver.component.core.interfaces.InterfaceMeta;
import org.xserver.component.exception.FilterProcessError;
import org.xserver.component.exception.ResouceNotFoundError;
import org.xserver.component.extension.filter.ContextFilter;
import org.xserver.component.extension.filter.DefaultContextFilterManager;
import org.xserver.component.extension.filter.FilterType;
import org.xserver.component.handler.ext.AbstractWriteHandler;
import org.xserver.component.handler.ext.ThrowableHandler;
import org.xserver.component.handler.ext.WriteHandlerManager;
import org.xserver.component.prototype.annotation.ReturnType;
import org.xserver.component.prototype.annotation.ReturnTypeEnum;
import org.xserver.component.prototype.processor.ReturnTypeProcessor;
import org.xserver.component.spring.SpringUtil;

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
	private static final Logger logger = LoggerFactory.getLogger(RequestDispatch.class);
	@Resource
	private InterfaceContext interfaceContext;
	@Resource
	private ThrowableHandler throwableHandler;
	@Resource
	private WriteHandlerManager writeHandlerManager;
	@Resource
	private DefaultContextFilterManager filterContextFactory;

	public void requestDispatch(XServerHttpContextAttachment attachment) {
		XServerHttpRequest request = attachment.getRequest();

		Object interfaceReturn = null;
		try {
			interfaceReturn = interfaceInvoke(attachment);
			//add by idol since 2016/02/23
			interfaceReturn = dealResult(attachment, interfaceReturn);
			//TODO

			writeHandlerManager.writeResponse(attachment, interfaceReturn);
		} catch (Throwable e) {
			/*
			 * The uniform exception action unit
			 */
			throwableHandler.handleThrowable(attachment, e);
			logger.error("Request [" + request.getUri() + "] invoke fail.", e);
		}
	}

	/**
	 * deal the Object result with Json or Xml Processor
	 * @param attachment
	 * @param obj
	 * @return
	 */
	private Object dealResult(XServerHttpContextAttachment attachment, Object obj) {
		String path = attachment.getRequest().getPath();
		InterfaceMeta interfaceMeta = interfaceContext.getInterfaceMeta(path);
		if (interfaceMeta == null) {
			throw new ResouceNotFoundError(HttpResponseStatus.BAD_REQUEST, "PATH \'" + path
					+ "\' CANNOT MATCH ANY INTERFACE.");
		}

		Method method = interfaceMeta.getMethod();
		ReturnType returnType = method.getAnnotation(ReturnType.class);
		if (returnType != null) {
			String value = returnType.value();
			ReturnTypeEnum rte = ReturnTypeEnum.valueOf(value.toUpperCase());
			ReturnTypeProcessor reTypePro = rte.getProcessor();
			if (reTypePro != null) {
				XServerHttpResponse response = attachment.getResponse();
				response.setContentTypeHeader(rte.getContentType() + "; " + XServerHttpResponse.DEFAULT_CHARSET);
				obj = reTypePro.returnProcess(obj);
			}
		}
		return obj;
	}

	/**
	 * call business logic process
	 * 
	 * @param attachment
	 * @return
	 * @throws Exception
	 */
	private Object interfaceInvoke(XServerHttpContextAttachment attachment) throws Exception {
		String path = attachment.getRequest().getPath();
		InterfaceMeta interfaceMeta = interfaceContext.getInterfaceMeta(path);
		if (interfaceMeta == null) {
			throw new ResouceNotFoundError(HttpResponseStatus.BAD_REQUEST, "PATH \'" + path
					+ "\' CANNOT MATCH ANY INTERFACE.");
		}

		Method method = interfaceMeta.getMethod();
		Class<?> clazz = interfaceMeta.getClazz();
		Object httpInterface = SpringUtil.getBean(StringUtil.toLowerCaseAtIndex(clazz.getSimpleName(), 0));
		XServerHttpRequest request = attachment.getRequest();
		if (attachment.isWebsocket()) {
			Channel channel = attachment.getChannelHandlerContext().getChannel();
			request.setChannel(channel);
		}

		@SuppressWarnings("unchecked")
		List<Object> objects = (List<Object>) attachment.getAttachment();
		Object[] parameters = objects.toArray(new Object[objects.size()]);

		return method.invoke(httpInterface, parameters);
	}

	/**
	 * the request go to business logic process, it should flow past
	 * matches filter(s). If there is no filter accept the request, the request
	 * will pass to next process(business logic). If there are some filters,
	 * matched filters need process the request in order. When one filter
	 * process error, the rest of filter and business logic should not receive
	 * the request.
	 * 
	 * @param attachment
	 * @throws Exception
	 */
	public void filterDispatch(XServerHttpContextAttachment attachment, FilterType filterType) throws Exception {
		String requestPath = attachment.getRequest().getPath();

		if (filterContextFactory == null) {
			return;
		}

		List<ContextFilter> filterChain = new ArrayList<ContextFilter>();
		if (filterType.equals(FilterType.BEFORE)) {
			filterChain = filterContextFactory.getBeforeFilterContext().get(requestPath);
		} else if (filterType.equals(FilterType.RETURN)) {
			filterChain = filterContextFactory.getReturnFilterContext().get(requestPath);
		}

		if (filterChain == null) {
			return;
		}

		for (ContextFilter filter : filterChain) {
			logger.info("Enter filter[{}]", filter);
			try {
				filter.contextProcess(attachment);
			} catch (FilterProcessError e) {
				logger.error("request[" + requestPath + "] go throught filter[" + e.getFilterName() + "] error", e);
				if (filter.isExceptionGoNext()) {
					continue;
				}

				throw e;
			}
		}

	}
}
