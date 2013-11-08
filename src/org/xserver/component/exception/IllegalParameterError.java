package org.xserver.component.exception;

/**
 * on generally, the <code>IllegalParameterError</code> apply to request
 * interface parameter error.
 * <p>
 * Condition:
 * <ul>
 * <li>Parameter Type Error, the type of provided cannot matches required type.</li>
 * </ul>
 * 
 * @author postonzhang
 * 
 */
public class IllegalParameterError extends AbstractServerError {
	public enum Type {
		Byte("Byte"), Char("Char"), Short("Short"), Integer("Integer"), Long(
				"Long"), Float("Float"), Double("Double"), Boolean("Boolean");

		private final String value;

		Type(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private static final long serialVersionUID = 1L;

	public IllegalParameterError(String message) {
		super(message);
	}

	public IllegalParameterError(String parameter, String value, Type type) {
		this("PARAMETER \'" + parameter + "\' VALUE IS \'" + value
				+ "\'. CANNOT MATCH TYPE " + type.getValue() + ".");
	}
}
