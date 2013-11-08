package org.xserver.common.util;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class Test {

	private static class User {
		private long id;
		private String name;
		private String avator240;
		private String avator160;
		private String address;

		public String getName() {
			return name;
		}

		public String getAddress() {
			return address;
		}

		public String getAvator240() {
			return avator240;
		}

		public String getAvator160() {
			return avator160;
		}

		public long getId() {
			return id;
		}
	}

	@JsonFilter("userFilter")
	private static interface UserFilterMixIn {

	}

	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		User user = new User();
		user.id = 1000L;
		user.name = "test name";
		user.avator240 = "240.jpg";
		user.avator160 = "160.jpg";
		user.address = "some address";

		FilterProvider idFilterProvider = new SimpleFilterProvider().addFilter(
				"userFilter",
				SimpleBeanPropertyFilter.filterOutAllExcept(new String[] {
						"name", "avator240" }));
		mapper.setFilters(idFilterProvider);
		String userFilterJson = mapper.writeValueAsString(user);

		System.out.println(userFilterJson);
	}
}