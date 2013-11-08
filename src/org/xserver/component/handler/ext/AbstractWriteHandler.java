package org.xserver.component.handler.ext;

import org.xserver.component.core.XServerHttpContextAttachment;

/**
 * This is abstract class for write response to client. The implement classes do
 * the real setContent and Channel write works.
 * 
 * @author postonzhang
 * 
 */
public abstract class AbstractWriteHandler {
	public abstract void writeContent(XServerHttpContextAttachment attachment,
			Object obj);
}
