package org.xserver.component.handler.ext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;

import javax.annotation.Resource;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.common.util.StringUtil;
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.exception.AbstractServerError;
import org.xserver.component.exception.ResouceNotFoundError;
import org.xserver.component.listener.XServerListener;

/**
 * <code>ThrowableHandler</code> handle {@link Throwable}, all business logic
 * throwable will go here. So do all throwable in here.
 * 
 * @author postonzhang
 * 
 */
@Component
public class ThrowableHandler {

	public static final Logger logger = LoggerFactory
			.getLogger(ThrowableHandler.class);

	@Resource
	private XServerHttpConfig xServerHttpConfig;
	@Resource
	private XServerListener xServerListener;

	public void handleThrowable(XServerHttpContextAttachment attachment,
			Throwable throwable) {
		XServerHttpResponse response = attachment.getResponse();
		String path = attachment.getRequest().getPath();

		xServerListener.incError(path);

		Throwable cause = throwable.getCause();
		String message = throwable.getClass().toString();
		if (throwable instanceof InvocationTargetException) {
			response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
			if (cause instanceof AbstractServerError) {
				message = cause.getMessage();
			}
		} else if (throwable instanceof AbstractServerError) {
			if (throwable instanceof ResouceNotFoundError) {
				message = throwable.getMessage();
				response.setStatus(HttpResponseStatus.BAD_REQUEST);
			}
		}

		String content = message;
		if (xServerHttpConfig.isDebug()) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);

			content = StringUtil.templateReplace(Adaptor.HTML_ADAPTOR, "#",
					new String[] { message, sw.toString() });

			pw.close();
		}

		response.setContentTypeHeader(XServerHttpResponse.ContentType.TEXT_HTML
				.getContentType() + "; " + XServerHttpResponse.DEFAULT_CHARSET);
		ChannelBuffer buffer = response.responseBuffer(content);
		response.setContentLengthHeader(buffer.readableBytes());
		response.setContent(buffer);
		Channel channel = attachment.getChannelHandlerContext().getChannel();
		ChannelFuture future = channel.write(response);
		future.addListener(attachment);

		// if (response.getStatus() == HttpResponseStatus.INTERNAL_SERVER_ERROR)
		// {
		// xServerListener.sendMessage("INTERFACE:" + path
		// + ",ERROR MESSAGE:" + message);
		// }
		String host = "no config";
		try {
			host = InetAddress.getLocalHost().toString();
		} catch (Exception e) {
			host = xServerHttpConfig.getHostname();
		}

		logger.error("host:[" + host + "] interface:["
				+ attachment.getRequest().getUri() + "] Exception:["
				+ throwable + "]", throwable);
	}
}
