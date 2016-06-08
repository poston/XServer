package org.xserver.component.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.core.RequestDispatch;
import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.extension.filter.FilterType;
import org.xserver.component.extension.filter.parameter.XServerParametersProcessFilter;
import org.xserver.component.websocket.XServerWebSocketServerHandshaker13;

/**
 * Interface dispatch
 * <p>
 * The request URL rule is
 * "http(s)://svr.xserver.com:port/className/methodName?param1=value1&amp;param2=value2"
 * .
 * </p>
 * Frame uses reflection to invoke interfaces method, all default wrapped
 * interfaces have two parameter, one is XServerHttpRequest, another is
 * XServerHttpResponse, this style like Servlet. The business logic use
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
	public static final Logger logger = LoggerFactory.getLogger(RequestDispatchHandler.class);
	@Resource
	private RequestDispatch requestDispatch;
	@Resource
	private XServerHttpConfig httpConfig;
	@Resource
	private XServerParametersProcessFilter xServerParametersProcessFilter;

	private Map<Integer, HttpPostRequestDecoder> context = new ConcurrentHashMap<Integer, HttpPostRequestDecoder>(1024);

	/**
	 * One request just enter this method only one(exclude HttpChunk)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		Object message = e.getMessage();
		if (message instanceof XServerHttpRequest) {
			XServerHttpRequest request = (XServerHttpRequest) message;

			request.setRemoteAddress(e.getRemoteAddress());
			request.setLocalAddress(e.getChannel().getLocalAddress());

			logger.info("Request from {}, {} {} requestURI:{}", new Object[] { e.getRemoteAddress(),
					request.getMethod().getName(), request.getProtocolVersion(), request.getUri() });
			ctx.setAttachment(request);

			XServerHttpContextAttachment attachment = new XServerHttpContextAttachment(request, ctx, httpConfig);

			/**
			 * because the webSocket handshake base on HTTP, so we add the
			 * logical action to HTTP handler
			 */
			if (isWebSocketHandshake(request)) {
				webSocketHandshake(ctx, e);
				attachment.setWebsocket(true);
			}

			/*
			 * if message is chunked, the server should accept all message body
			 * before into business logic
			 */
			if (request.isChunked()) {
				context.put(e.getChannel().getId(), request.getPostDecoder());
				return;
			}

			dispatch(attachment);
		}

		/*
		 * if the message is http chunk, we should append rest to the context
		 * message
		 */
		if (message instanceof HttpChunk) {
			HttpChunk httpChunk = (HttpChunk) message;

			XServerHttpRequest request = (XServerHttpRequest) ctx.getAttachment();
			HttpPostRequestDecoder decoder = context.get(e.getChannel().getId());
			decoder.offer(httpChunk);

			if (httpChunk.isLast()) {
				try {
					dispatch(new XServerHttpContextAttachment(request, ctx, httpConfig));
				} finally {
					context.remove(e.getChannel().getId());
				}
			} else {
				return;
			}
		}

		/*
		 * next upstream is WebSocket, if not WebSocket, it will upstream to
		 * next handler
		 */
		ctx.sendUpstream(e);
	}

	/**
	 * before filters -&gt business logic -&gt return filters
	 * @param attachment
	 * @throws Exception
	 */
	private void dispatch(XServerHttpContextAttachment attachment) throws Exception {
		requestDispatch.filterDispatch(attachment, FilterType.BEFORE);
		requestDispatch.requestDispatch(attachment);
		requestDispatch.filterDispatch(attachment, FilterType.RETURN);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		e.getChannel().close();
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		e.getChannel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		super.exceptionCaught(ctx, e);
		if (e.getCause() instanceof ReadTimeoutException) {
			XServerHttpResponse response = new XServerHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
			response.setContentString("Readtime out");
			e.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
		}
		logger.error("RequestDispatchHandler occur exception", e.getCause());
	}

	private WebSocketServerHandshaker webSocketHandshake(ChannelHandlerContext ctx, MessageEvent e) {
		XServerHttpRequest request = (XServerHttpRequest) e.getMessage();
		XServerWebSocketServerHandshaker13 handshaker = new XServerWebSocketServerHandshaker13(request.getUri(), null,
				false);
		handshaker.handshake(ctx.getChannel(), request).addListener(WebSocketServerHandshaker.HANDSHAKE_LISTENER);

		return handshaker;
	}

	/**
	 * The current request whether is WebSocket handshake, if WebSocket
	 * handshake return true, else return false.
	 * 
	 * At present, the check WebSocket handshake is necessary but not sufficient
	 * 
	 * @param request
	 *            current request
	 * @return whether WebSocket handshake
	 */
	private boolean isWebSocketHandshake(HttpRequest request) {
		String upgrade = request.headers().get(HttpHeaders.Names.UPGRADE);

		if (WebSocketHandler.WEBSOCKET.equalsIgnoreCase(upgrade)) {
			return true;
		}

		return false;
	}
}
