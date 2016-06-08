package org.xserver.component.extension.filter;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.core.interfaces.InterfaceContext;
import org.xserver.component.exception.FilterProcessError;
import org.xserver.component.spring.SpringUtil;

/**
 * The {@code AbstractContextFilter} class loads basic filter informations, and
 * it contains XServer {@link InterfaceContext} supports extend class to use. 
 * The default filter logic is that XServer will interrupt when any filter doing
 * contextProcess(do not continue do next filter, or business logic), if you
 * configure ignore exception it will go into next filter or business logic.
 * 
 * @author postonzhang
 * @since 2015/10/21
 * 
 */
public abstract class AbstractContextFilter implements ContextFilter {
	private static final Logger logger = LoggerFactory.getLogger(AbstractContextFilter.class);

	/**
	 * When the exceptionGoNext is true that invoke {@link #contextProcess(org.xserver.component.core.XServerHttpContextAttachment)} 
	 * will ignore exception and go next filter. If the value is false, the process will interrupt when call 
	 * {@link #contextProcess(org.xserver.component.core.XServerHttpContextAttachment)} throw exception 
	 */
	private boolean exceptionGoNext;
	/**
	 * The filter type decides what time to invoke {@link #contextProcess(org.xserver.component.core.XServerHttpContextAttachment)},
	 * if value set {@link FilterType#BEFORE} that will invoke before business logic, set {@link FilterType#RETURN} that will invoke 
	 * after business logic.
	 */
	private FilterType filterType;

	/**
	 * the interfaces context, subclass will use this context process in
	 * {@link AbstractContextFilter#contextProcess(org.xserver.component.core.XServerHttpContextAttachment)}
	 */
	private InterfaceContext interfaceContext;

	/**
	 * The default constructor set exceptionGoNext false and filterType before
	 */
	public AbstractContextFilter() {
		this(false, FilterType.BEFORE);
	}

	public AbstractContextFilter(boolean exceptionGoNext, FilterType filterType) {
		this.exceptionGoNext = exceptionGoNext;
		this.filterType = filterType;
	}

	@Override
	public boolean isExceptionGoNext() {
		return exceptionGoNext;
	}

	public void setExceptionGoNext(boolean exceptionGoNext) {
		this.exceptionGoNext = exceptionGoNext;
	}

	@Override
	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	/**
	 * although the method is not thread safe, but there is no possible for
	 * multithreading.
	 * 
	 * @return xserver interfaceContext
	 */
	public InterfaceContext getInterfaceContext() {
		if (interfaceContext == null) {
			interfaceContext = (InterfaceContext) SpringUtil.getBean(InterfaceContext.class);
		}

		return interfaceContext;
	}

	public void setInterfaceContext(InterfaceContext interfaceContext) {
		this.interfaceContext = interfaceContext;
	}

	/**
	 * we use the filter class simple name as the filter name 
	 */
	@Override
	public String getName() {
		return this.getClass().getSimpleName();

	}

	@Override
	public void contextInitialized() throws FilterProcessError {
	}

	@Override
	public void contextDestroyed() throws FilterProcessError {
	}

	@Override
	public void contextProcess(XServerHttpContextAttachment attachment) throws FilterProcessError {
		process(attachment);

		if (attachment.isWritable()) {
			writeResponse(attachment);
		}
	}

	protected abstract void process(XServerHttpContextAttachment attachment) throws FilterProcessError;

	/**
	 * write response to client and set cannot writable
	 * @param attachment
	 */
	protected void writeResponse(XServerHttpContextAttachment attachment) {
		attachment.setWritable(false);
		attachment.setWrote(true);

		Channel channel = attachment.getChannelHandlerContext().getChannel();

		if (channel != null && channel.isConnected() && channel.isOpen()) {
			XServerHttpResponse response = attachment.getResponse();

			String content = attachment.getAttachment().toString();

			// TODO remove
			if (response.getStatus() == null) {
				response.setStatus(HttpResponseStatus.OK);
			}
			response.setContentTypeHeader(XServerHttpResponse.ContentType.TEXT_PLAIN.getContentType() + "; "
					+ XServerHttpResponse.DEFAULT_CHARSET);

			ChannelBuffer buffer = response.responseBuffer(content);
			response.setContent(buffer);
			response.setContentLengthHeader(buffer.readableBytes());
			int contentLenth = content.length();
			if (contentLenth > 100) {
				content = content.substring(0, 50) + "...(" + contentLenth + ")..."
						+ content.substring(contentLenth - 50);
			}
			logger.info("Response for Request [{}] is {}", attachment.getRequest().getUri(), content);
		}
	}

	@Override
	public String toString() {
		return "ContextFilter [name=" + this.getClass().getSimpleName() + ", exceptionGoNext=" + exceptionGoNext
				+ ", filterType=" + filterType + "]";
	}

}
