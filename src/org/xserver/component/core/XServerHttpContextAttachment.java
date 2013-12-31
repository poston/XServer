package org.xserver.component.core;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.xserver.component.handler.XServerHttpInterceptor;
import org.xserver.component.spring.SpringUtil;

/**
 * <h3>HTTP Context</h3>
 * 
 * <pre>
 * struct{
 *     XServerHttpRequest request,
 *     XServerHttpResponse response,
 *     ChannelHandlerContext channelHandlerContext
 * }
 * </pre>
 * 
 * @author postonzhang
 * 
 */
public class XServerHttpContextAttachment implements ChannelFutureListener {
	private XServerHttpRequest request;
	private XServerHttpResponse response;
	private ChannelHandlerContext channelHandlerContext;
	private boolean comet;

	public XServerHttpContextAttachment(XServerHttpRequest request,
			XServerHttpResponse response,
			ChannelHandlerContext channelHandlerContext) {
		this.request = request;
		this.response = response;
		this.channelHandlerContext = channelHandlerContext;
	}

	public XServerHttpContextAttachment(XServerHttpRequest request,
			ChannelHandlerContext channelHandlerContext) {
		this(request, new XServerHttpResponse(), channelHandlerContext);
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

	public void setChannelHandlerContext(
			ChannelHandlerContext channelHandlerContext) {
		this.channelHandlerContext = channelHandlerContext;
	}

	public boolean isComet() {
		return comet;
	}

	public void setComet(boolean comet) {
		this.comet = comet;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		future.getChannel().close();
		XServerHttpConfig xServerHttpConfig = (XServerHttpConfig) SpringUtil
				.getBean(XServerHttpConfig.class);
		if (xServerHttpConfig.isInterceptor()) {
			XServerHttpInterceptor interceptor = ((XServerHttpInterceptor) SpringUtil
					.getBean(XServerHttpInterceptor.class));
			interceptor.dec(request.getPath());
		}
	}

}
