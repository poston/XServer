package org.xserver.test;

import org.xserver.component.annotation.ColumnFamily;

public class TestHBase {
	@ColumnFamily("colfam1")
	private String q1;

	public TestHBase() {

	}

	public String getQ1() {
		return q1;
	}

	public void setQ1(String q1) {
		this.q1 = q1;
	}

}
