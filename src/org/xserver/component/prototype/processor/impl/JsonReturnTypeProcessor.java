package org.xserver.component.prototype.processor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.component.json.JsonManager;
import org.xserver.component.prototype.processor.ReturnTypeProcessor;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Just do the result with JsonProcessor
 * @author Idol
 * @since  2016/02/22
 */
@Component
public class JsonReturnTypeProcessor implements ReturnTypeProcessor {
	private static final Logger log = LoggerFactory
			.getLogger(JsonReturnTypeProcessor.class);
	@Override
	public Object returnProcess(Object obj) {
		try {
			return  JsonManager.json(obj);
		} catch (JsonProcessingException e) {
			log.error("returnType mainly run wrong with JsonReturnTypeProcessor",e);
			return null;
		}
	}


}
