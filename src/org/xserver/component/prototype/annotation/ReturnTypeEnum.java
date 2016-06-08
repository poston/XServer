/**
 * 
 */
package org.xserver.component.prototype.annotation;

import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.prototype.processor.ReturnTypeProcessor;
import org.xserver.component.prototype.processor.impl.JsonReturnTypeProcessor;
import org.xserver.component.prototype.processor.impl.XmlReturnTypeProcessor;

/**
 * @author Idol
 * @since 2016年4月12日
 */
public enum ReturnTypeEnum {

	JSON("JSON", new JsonReturnTypeProcessor(),
			XServerHttpResponse.ContentType.APPLICATION_JSON.getContentType()), 
	XML("XML", new XmlReturnTypeProcessor(),
			XServerHttpResponse.ContentType.APPLICATION_XML.getContentType());

	private final String type;
	private final ReturnTypeProcessor processor;
	private final String contentType;

	ReturnTypeEnum(String type, ReturnTypeProcessor processor,
			String contentType) {
		this.type = type;
		this.processor = processor;
		this.contentType = contentType;
	}

	public String getType() {
		return type;
	}

	public ReturnTypeProcessor getProcessor() {
		return processor;
	}

	public String getContentType() {
		return contentType;
	}

}
