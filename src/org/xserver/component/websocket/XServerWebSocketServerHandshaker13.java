package org.xserver.component.websocket;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Values.WEBSOCKET;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.websocketx.WebSocket13FrameDecoder;
import org.jboss.netty.handler.codec.http.websocketx.WebSocket13FrameEncoder;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker13;
import org.jboss.netty.util.CharsetUtil;
import org.xserver.component.handler.RequestDispatchHandler;
import org.xserver.component.handler.XServerHttpInterceptor;
import org.xserver.component.handler.XServerHttpRequestDecoder;
import org.xserver.component.handler.XServerHttpResponseEncoder;

/**
 * The handshaker action between remote and server, operation as follow:
 * <p>
 * <strong>Receive request-&gtUpdate connection-&gtUpdate
 * {@link ChannelPipeline} </strong>
 * 
 * @author postonzhang
 * @since 2014/04/27
 * 
 */
public class XServerWebSocketServerHandshaker13 extends
		WebSocketServerHandshaker13 {

	public XServerWebSocketServerHandshaker13(String webSocketURL,
			String subprotocols, boolean allowExtensions) {
		super(webSocketURL, subprotocols, allowExtensions);
	}

	@Override
	public ChannelFuture handshake(Channel channel, HttpRequest req) {
		HttpResponse res = new DefaultHttpResponse(HTTP_1_1,
				HttpResponseStatus.SWITCHING_PROTOCOLS);

		String key = req.headers().get(Names.SEC_WEBSOCKET_KEY);
		if (key == null) {
			throw new WebSocketHandshakeException(
					"not a WebSocket request: missing key");
		}
		String acceptSeed = key + WEBSOCKET_13_ACCEPT_GUID;
		byte[] sha1;
		try {
			sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII
					.name()));
		} catch (UnsupportedEncodingException e) {
			return Channels.failedFuture(channel, e);
		}
		String accept = WebSocketUtil.base64(sha1);

		res.setStatus(HttpResponseStatus.SWITCHING_PROTOCOLS);
		res.headers().set(Names.UPGRADE, WEBSOCKET.toLowerCase());
		res.headers().set(Names.CONNECTION, Names.UPGRADE);
		res.headers().set(Names.SEC_WEBSOCKET_ACCEPT, accept);
		String subprotocols = req.headers().get(Names.SEC_WEBSOCKET_PROTOCOL);
		if (subprotocols != null) {
			String selectedSubprotocol = selectSubprotocol(subprotocols);
			if (selectedSubprotocol == null) {
				throw new WebSocketHandshakeException(
						"Requested subprotocol(s) not supported: "
								+ subprotocols);
			} else {
				res.headers().set(Names.SEC_WEBSOCKET_PROTOCOL,
						selectedSubprotocol);
				setSelectedSubprotocol(selectedSubprotocol);
			}
		}

		/*
		 * Upgrade the connection and send the handshake response
		 */
		ChannelFuture future = channel.write(res);

		/*
		 * At present, the channel(connection) between remote and server is
		 * WebSocket, we remove needless handler at this channel.
		 */
		ChannelPipeline p = channel.getPipeline();
		if (p.get(HttpChunkAggregator.class) != null) {
			p.remove(HttpChunkAggregator.class);
		}

		if (p.get(RequestDispatchHandler.class) != null) {
			p.remove(RequestDispatchHandler.class);
		}

		if (p.get(XServerHttpInterceptor.class) != null) {
			p.remove(XServerHttpInterceptor.class);
		}

		p.replace(XServerHttpRequestDecoder.class, "WebSocketDecoder",
				new WebSocket13FrameDecoder(true, false,
						getMaxFramePayloadLength()));
		p.replace(XServerHttpResponseEncoder.class, "WebSocketEncoder",
				new WebSocket13FrameEncoder(false));

		return future;
	}

}
