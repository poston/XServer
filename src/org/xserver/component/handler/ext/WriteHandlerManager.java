package org.xserver.component.handler.ext;

import org.springframework.stereotype.Component;
import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.spring.SpringUtil;

@Component
public class WriteHandlerManager {
	public enum WriteType {
		JSON, HTML, XML, NULL;
	}

	private AbstractWriteHandler abstractWriteHandler;

	public AbstractWriteHandler writerHandler(XServerHttpResponse response) {
		/**
		 * java had to enhance its specification and enums in switch behave
		 * differently than regular switch statements in two significant and
		 * non-trivial ways.
		 */
		WriteType writeType = response.getWriteType();
		switch (writeType) {
		case JSON:
			return (AbstractWriteHandler) SpringUtil
					.getBean(TextPlainWriteHandler.class);
		case HTML:
			return (AbstractWriteHandler) SpringUtil
					.getBean(HtmlWriterHandler.class);
		case XML:
			return (AbstractWriteHandler) SpringUtil
					.getBean(TextPlainWriteHandler.class);
		case NULL:
			return (AbstractWriteHandler) SpringUtil
					.getBean(ResponseWriteHandler.class);
		default:
			return null;
		}
	}

	public void writeResponse(XServerHttpContextAttachment attachment,
			Object obj) {
		abstractWriteHandler = writerHandler(attachment.getResponse());
		abstractWriteHandler.writeContent(attachment, obj);
	}

	public AbstractWriteHandler getAbstractWriteHandler() {
		return abstractWriteHandler;
	}

	public void setAbstractWriteHandler(
			AbstractWriteHandler abstractWriteHandler) {
		this.abstractWriteHandler = abstractWriteHandler;
	}

}
