package org.xserver.component.handler;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Handle webSocket
 * 
 * @author postonzhang
 * @since 2014/04/22
 */
@Component
public class WebSocketHandler extends SimpleChannelUpstreamHandler {

	private static final Logger log = LoggerFactory
			.getLogger(WebSocketHandler.class);

	public static final String WEBSOCKET = "websocket";

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object message = e.getMessage();
		if (message instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, e);
		}
		ctx.sendUpstream(e);
	}

	/**
	 * There will deal three {@link WebSocketFrame},
	 * <ul>
	 * <li>{@link CloseWebSocketFrame} that we just close the channel;</li>
	 * <li>{@link PingWebSocketFrame} that we return binary data;</li>
	 * <li>{@link TextWebSocketFrame} that we dispatch to business executor;</li>
	 * </ul>
	 * 
	 * @param ctx
	 * @param e
	 */
	private void handleWebSocketFrame(ChannelHandlerContext ctx, MessageEvent e) {
		Object frame = e.getMessage();
		if (frame instanceof CloseWebSocketFrame) {
			ChannelFuture f = ctx.getChannel().write(frame);
			f.addListener(ChannelFutureListener.CLOSE);
			log.info("client {} close connection", e.getChannel()
					.getRemoteAddress());
			return;
		}

		if (frame instanceof PingWebSocketFrame) {
			ctx.getChannel().write(
					new PongWebSocketFrame(((PingWebSocketFrame) frame)
							.getBinaryData()));
			return;
		}

		if (!(frame instanceof TextWebSocketFrame)) {
			log.error("{} frame types not supported", frame.getClass()
					.getName());
			throw new UnsupportedOperationException(String.format(
					"%s frame types not supported", frame.getClass().getName()));
		}

		// TODO TextWebSocketFrame need refactor 2014/04/22
		TextWebSocketFrame twsf = (TextWebSocketFrame) frame;
		String content = twsf.getText();
		ctx.getChannel().write(new TextWebSocketFrame(content));
		log.info("response text {}", content);
	}
}
