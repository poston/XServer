package org.xserver.component.handler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

/**
 * XServer Http encoder, see {@link HttpResponseEncoder}
 * 
 * @author postonzhang
 * @since 2013/01/08
 */
public class XServerHttpResponseEncoder extends HttpResponseEncoder {

	@Override
	protected void encodeInitialLine(ChannelBuffer buf, HttpMessage message)
			throws Exception {
		super.encodeInitialLine(buf, message);
	}
}
