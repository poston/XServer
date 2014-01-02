package org.xserver.component.handler.ext;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.springframework.stereotype.Component;
import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.core.XServerHttpResponse;

@Component
public class ResponseWriteHandler extends AbstractWriteHandler {

	@Override
	public void writeContent(XServerHttpContextAttachment attachment, Object obj) {
		//TO NOTHING HERE;
	}

}
