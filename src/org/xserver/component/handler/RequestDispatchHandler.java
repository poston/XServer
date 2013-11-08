package org.xserver.component.handler;

import javax.annotation.Resource;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.component.core.RequestDispatch;
import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;

/**
 * Interface dispatch
 * <p>
 * The request URL rule is
 * "http(s)://svr.xserver.com:port/className/methodName?param1=value1&amp;param2=value2"
 * .
 * </p>
 * 
 * Frame uses reflection to invoke interface method, all wrapped interface have
 * two parameter, one is XServerHttpRequest, another is XServerHttpResponse,
 * this style like Servlet. The business logic use
 * <code>XServerHttpRequest</code>'s <code>getParameter</code>() to grain
 * parameter(s).
 * 
 * the workflow as follow:
 * 
 * <pre>
 *                         XServer
 *               +------------------------+
 *               |        Encoder         |
 *            |  |     Business Logic     | /|\
 * downstream |  |     RequestDispath     |  | upstream
 *           \|/ |        Decoder         |  |
 *               +------------------------+
 *                          Client
 * 
 * </pre>
 * 
 * @author postonzhang
 * @since 2013/01/10
 */

@Component
public class RequestDispatchHandler extends SimpleChannelUpstreamHandler {
	public static final Logger logger = LoggerFactory
			.getLogger(RequestDispatchHandler.class);
	@Resource
	private RequestDispatch requestDispatch;

	// private static final ChannelGroup group = new DefaultChannelGroup(
	// "XServer-Comet");
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object obj = e.getMessage();
		if (obj instanceof XServerHttpRequest) {
			XServerHttpRequest request = (XServerHttpRequest) obj;
			request.setRemoteAddress(e.getRemoteAddress());
			request.setLocalAddress(e.getChannel().getLocalAddress());

			if (request.getPath().equals("/favicon.ico")) {
				e.getChannel().close();
				return;
			}

			logger.info("Request from {}, {} {} requestURI:{}", new Object[] {
					e.getRemoteAddress(), request.getMethod().getName(),
					request.getProtocolVersion(), request.getUri() });
			requestDispatch.requestDispatch(new XServerHttpContextAttachment(
					request, ctx));
		}
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		e.getChannel().close();
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		// TODO Auto-generated method stub
		super.channelDisconnected(ctx, e);
		e.getChannel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		super.exceptionCaught(ctx, e);
		if (e.getCause() instanceof ReadTimeoutException) {
			XServerHttpResponse response = new XServerHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
			response.setContentString("Readtime out");
			e.getChannel().write(response);
		}
		logger.error("RequestDispatchHandler occur exception", e.getCause());
		e.getChannel().close();
	}
}
