package org.xserver.component.hbase.exception;

import org.springframework.core.NestedRuntimeException;

public class ClassTypeNotSupportException extends NestedRuntimeException {

	private static final long serialVersionUID = 8660571143348227060L;

	public ClassTypeNotSupportException(String msg) {
		super(msg);
	}

	public ClassTypeNotSupportException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
