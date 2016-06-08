package org.xserver.component.core;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.handler.XServerHttpInterceptor;
import org.xserver.component.spring.SpringUtil;

/**
 * <h3>HTTP Context</h3>
 * 
 * <pre>
 * main struct{
 *     XServerHttpRequest request,
 *     XServerHttpResponse response,
 *     ChannelHandlerContext channelHandlerContext,
 *     Map attachment
 * }
 * </pre>
 * 
 * @author postonzhang
 * 
 */
public class XServerHttpContextAttachment implements ChannelFutureListener {
	/**
	 * the server configuration {@link XServerHttpConfig}
	 */
	private XServerHttpConfig httpConfig;
	/**
	 * the request from client
	 */
	private XServerHttpRequest request;
	/**
	 * the response from server
	 */
	private XServerHttpResponse response;
	/**
	 * channel context
	 */
	private ChannelHandlerContext channelHandlerContext;
	/**
	 * the request flow context attachment
	 */
	private Map<String, Object> attachment;
	private boolean websocket;
	/**
	 * if true response will write to client
	 */
	private boolean writable;
	/**
	 * if had wrote response to client the flag is true 
	 */
	private boolean wrote;

	public XServerHttpContextAttachment(XServerHttpRequest request, XServerHttpResponse response,
			ChannelHandlerContext channelHandlerContext, XServerHttpConfig httpConfig) {
		this(request, channelHandlerContext, httpConfig);
	}

	public XServerHttpContextAttachment(XServerHttpRequest request, ChannelHandlerContext channelHandlerContext,
			XServerHttpConfig httpConfig) {
		this(request, new XServerHttpResponse(), channelHandlerContext, httpConfig, false);
	}

	public XServerHttpContextAttachment(XServerHttpRequest request, XServerHttpResponse response,
			ChannelHandlerContext channelHandlerContext, XServerHttpConfig httpConfig, boolean websocket) {
		this.request = request;
		this.response = response;
		this.channelHandlerContext = channelHandlerContext;
		this.httpConfig = httpConfig;
		this.websocket = websocket;
	}

	public XServerHttpRequest getRequest() {
		return request;
	}

	public void setRequest(XServerHttpRequest request) {
		this.request = request;
	}

	public XServerHttpResponse getResponse() {
		return response;
	}

	public void setResponse(XServerHttpResponse response) {
		this.response = response;
	}

	public ChannelHandlerContext getChannelHandlerContext() {
		return channelHandlerContext;
	}

	public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
		this.channelHandlerContext = channelHandlerContext;
	}

	public boolean isWebsocket() {
		return websocket;
	}

	public void setWebsocket(boolean websocket) {
		this.websocket = websocket;
	}

	public Map<String, Object> getAttachment() {
		return attachment;
	}

	public void setAttachment(Map<String, Object> attachment) {
		this.attachment = attachment;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	public boolean hasWrote() {
		return wrote;
	}

	public void setWrote(boolean wrote) {
		this.wrote = wrote;
	}

	/**
	 * put attachment to context, for example filter can put result to the context that maybe useful for following filter(s) or business logic
	 * @param attachment
	 */
	public void attachment(String key, Object attachment) {
		if (this.attachment == null) {
			this.attachment = new HashMap<String, Object>(8);
		}

		this.attachment.put(key, attachment);
	}

	/**
	 * when the write operation complete the method will invoke that close the connection
	 */
	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		future.getChannel().close();

		if (httpConfig.isInterceptor()) {
			ChannelPipeline channelPipeline = channelHandlerContext.getPipeline();
			if (channelPipeline.getContext(XServerHttpInterceptor.class.getSimpleName()) != null) {
				XServerHttpInterceptor interceptor = ((XServerHttpInterceptor) SpringUtil
						.getBean(XServerHttpInterceptor.class));
				interceptor.dec(request.getPath());
			}
		}
	}
}
