package org.xserver.component.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The common logger is used to print log info to <code>STDOUT</code>.
 * 
 * @author postonzhang
 * 
 */
@Component
public class StdOutLog {
	private static final Logger logger = LoggerFactory
			.getLogger(StdOutLog.class);

	public static Logger getLogger() {
		return logger;
	}

}
