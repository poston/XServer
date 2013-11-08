package org.xserver.common.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonEngine {
	public static final ObjectMapper DEFAULT_JACKSON_MAPPER;
	public static final ObjectMapper FILTER_JACKSON_MAPPER;

	public static final boolean JACKSON_ENABLE = ReflectionUtil
			.classFound(new String[] { ObjectMapper.class.getName(),
					JsonGenerator.class.getName() });

	static {
		if (JACKSON_ENABLE) {
			JsonFactory jf = new JsonFactory();
			jf.enable(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);
			jf.enable(Feature.ALLOW_COMMENTS);
			jf.enable(Feature.ALLOW_NON_NUMERIC_NUMBERS);
			jf.enable(Feature.ALLOW_SINGLE_QUOTES);
			jf.enable(Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
			jf.enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);

			DEFAULT_JACKSON_MAPPER = new ObjectMapper(jf);
			DEFAULT_JACKSON_MAPPER.configure(
					SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			FILTER_JACKSON_MAPPER = new ObjectMapper(jf);
			FILTER_JACKSON_MAPPER
					.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			FILTER_JACKSON_MAPPER.configure(
					SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		} else {
			DEFAULT_JACKSON_MAPPER = null;
			FILTER_JACKSON_MAPPER = null;
		}
	}
}
