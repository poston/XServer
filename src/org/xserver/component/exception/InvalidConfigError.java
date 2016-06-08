package org.xserver.component.exception;

public class InvalidConfigError extends AbstractServerError {
	private static final long serialVersionUID = 1L;

	public InvalidConfigError(String fileName, String configName, String message) {
		super("Config '" + configName + "' at '" + fileName + "' invalid, "
				+ message);
	}
}
