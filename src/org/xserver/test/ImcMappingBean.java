package org.xserver.test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.xserver.common.util.JsonEngine;
import org.xserver.component.json.JsonManager;
import org.xserver.component.json.util.JsonUtil;

import com.fasterxml.jackson.databind.JavaType;

public class ImcMappingBean {
	private int imcNum;
	private String centerId;
	private String businessGroupId;

	public int getImcNum() {
		return imcNum;
	}

	public void setImcNum(int imcNum) {
		this.imcNum = imcNum;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getBusinessGroupId() {
		return businessGroupId;
	}

	public void setBusinessGroupId(String businessGroupId) {
		this.businessGroupId = businessGroupId;
	}

	@Override
	public String toString() {
		return "ImcMappingBean [imcNum=" + imcNum + ", centerId=" + centerId
				+ ", businessGroupId=" + businessGroupId + "]";
	}

	private static final String URL_IMC = "http://10.133.8.94:8080/reporterCache/imc";

	private static Map<Integer, ImcMappingBean> imcCache() throws IOException {
		JavaType javaType = JsonEngine.DEFAULT_JACKSON_MAPPER.getTypeFactory()
				.constructParametricType(HashMap.class, String.class,
						ImcMappingBean.class);
		Map<Integer, ImcMappingBean> imcCache = JsonEngine.DEFAULT_JACKSON_MAPPER
				.readValue(new URL(URL_IMC), javaType);

		return imcCache;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(imcCache().get(400991));
	}
}
