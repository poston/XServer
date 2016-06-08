package org.xserver.component.core;

import java.net.SocketAddress;

/**
 * In spring of 2013, XServer project start up. At that time, XServer just
 * support HTTP prototype. For now, Bigger XServer support Protocol, More
 * complex system build. So we abstract some common operation and properties.
 * The <code>Request</code> interface is common for all request.
 * 
 * @author postonzhang
 * @since 2014/04/27
 * 
 */
public interface Request {
	public enum RequestType {
		/** User Datagram Protocol */
		UDP("udp"),
		/** Transmission Control Protocol */
		TCP("tcp"),
		/** Hyper Text Transport Protocol (Secure) */
		HTTP("http"),
		/** WebSocket Protocol */
		WEBSOCKET("websocket");

		private final String requestType;

		RequestType(String requestType) {
			this.requestType = requestType;
		}

		public String getRequestType() {
			return requestType;
		}
	}

	public RequestType getRequestType();

	public SocketAddress getRemoteAddress();

	public SocketAddress getLocalAddress();
}
