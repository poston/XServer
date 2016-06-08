package org.xserver.component.websocket;

import org.jboss.netty.handler.codec.http.websocketx.WebSocket13FrameDecoder;

public class XServerWebSocket13FrameDecoder extends WebSocket13FrameDecoder {

	public XServerWebSocket13FrameDecoder(boolean maskedPayload,
			boolean allowExtensions) {
		super(maskedPayload, allowExtensions);
	}
	
	
}
