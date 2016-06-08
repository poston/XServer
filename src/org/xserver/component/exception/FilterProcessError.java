package org.xserver.component.exception;

public class FilterProcessError extends AbstractServerError {

	private static final long serialVersionUID = -8098269500124759892L;

	private String filterName;

	public FilterProcessError(String filterName, String message) {
		super("In filter \"" + filterName + "\" process error, " + message);
		this.filterName = filterName;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

}
