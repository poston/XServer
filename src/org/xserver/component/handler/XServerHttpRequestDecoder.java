package org.xserver.component.handler;

import org.jboss.netty.handler.codec.http.HttpMessageDecoder;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.xserver.component.core.XServerHttpRequest;

/**
 * Override supperClass {@link HttpMessageDecoder}'s <code>createMessage</code>
 * method. The initialize XServerHttpRequest rely on the http prototype like:
 * 
 * <h3>Http Prototype</h3>
 * 
 * <pre>
 * +-----------------------+     +-------------------------------------------------------------------------------------+
 * |Method URI HttpVersion |     | GET /httpInterfaceClass/httpinterfaceMethod?callback=jQuery41786908986_13432487&id=1|
 * |Host: ...              |     | Host: svr.xserver.qq.com:8080                                                       |
 * |User-Agent:...         | --->| User-Agent: Mozilla/5.0 (Windows NT 6.1: rv:15.0) Gecko/20100101 Firefox/15.0       |
 * |.....                  |     | .......                                                                             |
 * +-----------------------+     +-------------------------------------------------------------------------------------+
 * </pre>
 * 
 * Default XServerHttpRequestDecoder instance set its maxInitialLineLength 4096
 * bytes, maxHeaderSize 65536 bytes, maxChunkSize 65536 bytes
 * 
 * @author postonzhang
 * 
 */
public class XServerHttpRequestDecoder extends HttpMessageDecoder {
	public XServerHttpRequestDecoder() {
		super(4096, 65536, 65536);
	}

	/**
	 * @param initialLine
	 *            Http request prototype first line, [Method URI HttpVersion]
	 */
	@Override
	protected XServerHttpRequest createMessage(String[] initialLine)
			throws Exception {
		return new XServerHttpRequest(HttpVersion.valueOf(initialLine[2]),
				HttpMethod.valueOf(initialLine[0]), initialLine[1]);
	}

	@Override
	protected boolean isDecodingRequest() {
		return true;
	}
}
