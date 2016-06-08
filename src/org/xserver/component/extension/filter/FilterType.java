package org.xserver.component.extension.filter;

public enum FilterType {
	BEFORE("before"), RETURN("return");

	private String type;

	FilterType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
