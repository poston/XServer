package org.xserver.component.handler.ext;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.common.util.StringUtil;
import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;

@Component
public class TextPlainWriteHandler extends AbstractWriteHandler {

	public static final String CALLBACK = "callback";
	public static final String BRACKET_LEFT = "(";
	public static final String BRACKET_RIGHT = ")";
	private static final Logger logger = LoggerFactory
			.getLogger(TextPlainWriteHandler.class);

	@Override
	public void writeContent(XServerHttpContextAttachment attachment, Object obj) {
		XServerHttpResponse response = attachment.getResponse();
		XServerHttpRequest request = attachment.getRequest();
		String callback = request.getParameter(CALLBACK);

		String content = obj.toString();
		if (!StringUtil.isEmpty(callback)) {
			content = generateJsonp(callback, content);
		}
		response.setStatus(HttpResponseStatus.OK);
		response.setContentTypeHeader(XServerHttpResponse.ContentType.TEXT_PLAIN
				.getContentType() + "; " + XServerHttpResponse.DEFAULT_CHARSET);

		ChannelBuffer buffer = response.responseBuffer(content);
		response.setContent(buffer);
		response.setContentLengthHeader(buffer.readableBytes());
		int contentLenth = content.length();
		if (contentLenth > 100) {
			content = content.substring(0, 50) + "...(" + contentLenth + ")..."
					+ content.substring(contentLenth - 50);
		}
		logger.info("Response for Request [{}] is {}", attachment.getRequest()
				.getUri(), content);

		Channel channel = attachment.getChannelHandlerContext().getChannel();
		ChannelFuture future = channel.write(response);
		future.addListener(attachment);
	}

	private String generateJsonp(String callback, String json) {
		return callback + BRACKET_LEFT + json + BRACKET_RIGHT;
	}
}
