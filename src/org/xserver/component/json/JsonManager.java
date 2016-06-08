package org.xserver.component.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.common.util.JsonEngine;
import org.xserver.common.util.ReflectionUtil;
import org.xserver.common.util.StringUtil;
import org.xserver.component.json.util.JsonUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Json to XServerHttpResponse, the class support {@code mapper} method to
 * generate Object Json.
 * 
 * @author postonzhang
 * 
 */
public class JsonManager {

	private static final Logger log = LoggerFactory
			.getLogger(JsonManager.class);
	
	public static final String JSON_FILTER_EXCEPT_FIELD = "except-field-filter";

	public static String mapper(Object[] targets) {
		if (StringUtil.isEmpty(targets)) {
			return "{}";
		}

		if (targets.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Argument should be key-value pair.");
		}

		StringBuilder result = new StringBuilder();
		result.append(JsonUtil.LEFT_BRACE);
		for (int i = 0; i < targets.length; i++) {
			if (i % 2 == 0) {
				result.append(JsonUtil.QUOTATION).append(targets[i])
						.append(JsonUtil.QUOTATION).append(JsonUtil.COLON);
			} else {
				if (targets[i] instanceof String) {
					String value = (String) targets[i];
					if (value.startsWith(JsonUtil.LEFT_BRACE)
							|| value.startsWith(JsonUtil.LEFT_BRACKET)) {
						result.append(value).append(JsonUtil.COMMA);
						continue;
					}
				}
				try {
					result.append(
							JsonEngine.DEFAULT_JACKSON_MAPPER
									.writeValueAsString(targets[i])).append(
							JsonUtil.COMMA);
				} catch (JsonProcessingException e) {
					log.error("json process error", e);
				}
			}
		}
		return result.substring(0, result.length() - 1) + JsonUtil.RIGHT_BRACE;
	}

	/**
	 * Serialized specified bean to json
	 * 
	 * @param target
	 *            specified bean
	 * @return json data
	 * @throws JsonProcessingException
	 */
	public static String json(Object target) throws JsonProcessingException {
		// when XServer use jsonExceptField to get json, must add the
		// @JsonFilter annotation to the JavaBean, According to this when we
		// want to serialize all field in the JavaBean will cause a
		// JsonMappingException: Can not resolve BeanPropertyFilter with id
		// 'exception-field-filter'. Then put the
		// DEFAULT_JACKSON_MAPPER.writeValueAsString and jsonExceptField(target,
		// new String[]{}) in try-catch block will solve the problem
		try {
			return JsonEngine.DEFAULT_JACKSON_MAPPER.writeValueAsString(target);
		} catch (JsonMappingException e) {
			return jsonExceptField(target, new String[] {});
		}
	}

	/**
	 * Serialized specified bean to json
	 *
	 * @param target
	 *            specified bean
	 * @return json data
	 * @throws JsonProcessingException
	 */
	public static String jsonFilter(Object target) throws JsonProcessingException {
		// when XServer use jsonExceptField to get json, must add the
		// @JsonFilter annotation to the JavaBean, According to this when we
		// want to serialize all field in the JavaBean will cause a
		// JsonMappingException: Can not resolve BeanPropertyFilter with id
		// 'exception-field-filter'. Then put the
		// DEFAULT_JACKSON_MAPPER.writeValueAsString and jsonExceptField(target,
		// new String[]{}) in try-catch block will solve the problem
		try {
			return JsonEngine.FILTER_JACKSON_MAPPER.writeValueAsString(target);
		} catch (JsonMappingException e) {
			return jsonExceptField(target, new String[] {});
		}
	}

	/**
	 * Serialized bean to json, but exclude some fields of specified bean target
	 * 
	 * @param target
	 *            specified bean
	 * @param fields
	 *            exclude field name
	 * @return json data
	 * @throws JsonProcessingException
	 */
	public static String jsonExceptField(Object target, String[] fields)
			throws JsonProcessingException {
		FilterProvider fp = new SimpleFilterProvider().addFilter(
				JSON_FILTER_EXCEPT_FIELD,
				SimpleBeanPropertyFilter.serializeAllExcept(fields));
		JsonEngine.FILTER_JACKSON_MAPPER.setFilters(fp);
		return JsonEngine.FILTER_JACKSON_MAPPER.writeValueAsString(target);
	}

	/**
	 * Serialized bean to json, just include specified fields
	 * 
	 * @param target
	 *            target bean
	 * @param clazz
	 *            the serialized bean class
	 * @param fields
	 *            included fields
	 * @return json data
	 * @throws JsonProcessingException
	 */
	public static String jsonIncludeField(Object target, Class<?> clazz,
			String[] fields) throws JsonProcessingException {
		String[] exceptFields = ReflectionUtil.exceptFields(clazz, fields);
		return jsonExceptField(target, exceptFields);
	}

	public static <T> T getBean(String json, Class<T> clazz) {
		T t = null;
		try {
			t = JsonEngine.DEFAULT_JACKSON_MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			log.error("Serialized json to '" + clazz.getName()
					+ "' occur error.", e);
		}

		return t;
	}
	
	public static <T> T getCompositeObject(String json, TypeReference<T> typeReference){
		T t = null;
		try {
			t = JsonEngine.DEFAULT_JACKSON_MAPPER.readValue(json, typeReference);
		} catch (Exception e) {
			log.error("Serialized json to '" + typeReference
					+ "' occur error.", e);
		} 
		
		return t;
	}
}
