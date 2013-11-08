package org.xserver.component.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
	/**
	 * Get logger
	 * 
	 * @param loggerName
	 *            string loggerName
	 * @return logger
	 */
	public static Logger getLogger(String loggerName) {
		return LoggerFactory.getLogger(loggerName);
	}

	/**
	 * <pre>
	 * +----------------------+
	 * |      Target Class    |
	 * |----------------------+
	 * |        LogUtil       |
	 * |----------------------+
	 * |   Always is Thread   |
	 * +----------------------+
	 * </pre>
	 * 
	 * @return current class logger
	 */
	public static Logger getLogger() {
		return LoggerFactory
				.getLogger(Thread.currentThread().getStackTrace()[2]
						.getClassName());
	}

	/**
	 * Get logger rely on target
	 * 
	 * @param target
	 *            target logger
	 * @return logger
	 */
	public static Logger getLogger(Object target) {
		if (target instanceof Class<?>) {
			return LoggerFactory.getLogger((Class<?>) target);
		}

		return LoggerFactory.getLogger(target.getClass().getName());
	}
}
