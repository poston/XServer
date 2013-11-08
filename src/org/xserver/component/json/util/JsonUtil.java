package org.xserver.component.json.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xserver.common.util.StringUtil;
import org.xserver.component.annotation.Chart;
import org.xserver.component.annotation.Description;
import org.xserver.component.json.JsonManager;
import org.xserver.interfaces.inner.Serie;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The util class support common works for json. such as get JavaBean field
 * description, convent a list javaBean to chart datum.
 * 
 * @author postonzhang
 * 
 */
public class JsonUtil {
	public static final String QUOTATION = "\"";
	public static final String COLON = ":";
	public static final String COMMA = ",";
	public static final String LEFT_BRACE = "{";
	public static final String RIGHT_BRACE = "}";
	public static final String LEFT_BRACKET = "[";
	public static final String RIGHT_BRACKET = "]";

	public static final String CATEGORIES = "categories";
	public static final String SERIES = "series";
	public static final String TITLE = "title";

	public static final String HEAD = "head";
	public static final String MESSAGE = "msg";
	public static final String COUNT = "count";
	public static final String LIST = "list";
	public static final String PAGE = "page";
	public static final String BEGIN_DATE = "beginDate";
	public static final String END_DATE = "endDate";
	public static final String RESULT_CODE = "result_code";

	public static enum JsonType {
		Array, Map
	}

	public static class Pair<K, V> {
		private K key;
		private V value;

		public Pair() {

		}

		public Pair(K k, V v) {
			this.key = k;
			this.value = v;
		}

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}

	}

	/**
	 * 
	 * @param target
	 *            javaBean target
	 * @return only a list of javaBean fields description
	 */
	public static List<Object> getBeanDescription(Object target) {
		return getBeanDescription(target, JsonType.Array);
	}

	public static List<Object> getBeanDescriptionIncludeFields(Object target,
			String[] fields) {
		return getBeanDescription(target, fields, false);
	}

	/**
	 * 
	 * @param target
	 *            javaBean target with {@link Description} annotation
	 * @param type
	 *            Array or Map
	 * @return a list of javaBean fields description, if type is JsonType.Array
	 *         just return a string array about javaBean fields name, else type
	 *         is JsonType.Map return a list of Pairs which one is a unit with
	 *         name and description of javaBean's field.
	 */
	public static List<Object> getBeanDescription(Object target, JsonType type) {
		switch (type) {
		case Array:
			return getBeanDescription(target, null, false);
		case Map:
			return getBeanDescription(target, null, true);
		default:
			return null;
		}
	}

	/**
	 * The util method is for generate chart datum from list, the front use <a
	 * href="www.highcharts.com">HighChart</a> as drawing tools.
	 * 
	 * @param list
	 *            most is from DB select datum
	 * @return a map with categories and series data
	 * @throws Exception
	 *             use reflection to set entry value exception
	 */
	public static Map<String, Object> convertChart(List<?> list)
			throws Exception {
		if (list == null || list.size() == 0) {
			return null;
		}

		Map<String, Object> result = new HashMap<String, Object>(2);

		List<String> categories = new ArrayList<String>(list.size());
		Object obj = list.get(0);
		Field[] fields = obj.getClass().getDeclaredFields();

		for (Object entry : list) {
			for (Field field : fields) {
				if (!field.getAnnotation(Chart.class).value()) {
					field.setAccessible(true);
					categories.add((String) field.get(entry));
				}
			}
		}
		result.put(CATEGORIES, categories);

		List<Serie> series = new ArrayList<Serie>(list.size());
		for (Field field : fields) {
			if (field.getAnnotation(Chart.class).value()) {
				Serie serie = new Serie();
				serie.setName(field.getAnnotation(Chart.class).name());

				for (Object entry : list) {
					if (serie.getDatas() == null) {
						List<Object> data = new ArrayList<Object>();
						field.setAccessible(true);
						data.add(field.get(entry));
						serie.setDatas(data);
					} else {
						serie.getDatas().add(field.get(entry));
					}
				}
				series.add(serie);
			}
		}
		result.put(SERIES, series);

		return result;
	}

	/**
	 * 
	 * @param target
	 *            javaBean target
	 * @param detail
	 *            whether output javaBean detail to list
	 * @return a list of javaBean fields description, if detail is true, return
	 *         a list of pairs of name and its description, else return a string
	 *         array about name
	 */
	private static List<Object> getBeanDescription(Object target,
			String[] includeFields, boolean detail) {
		List<Object> result = new ArrayList<Object>();
		Field[] fields = target.getClass().getDeclaredFields();

		if (StringUtil.isEmpty(fields)) {
			return result;
		}

		Set<String> set = null;
		if (!StringUtil.isEmpty(includeFields)) {
			set = new HashSet<String>(Arrays.asList(includeFields));
		}
		for (Field field : fields) {
			if (set != null) {
				if (!set.contains(field.getName())) {
					continue;
				}
			}
			if (field.isAnnotationPresent(Description.class)) {
				String description = field.getAnnotation(Description.class)
						.value();

				if (detail) {
					Pair<String, String> pair = new Pair<String, String>(
							field.getName(), description);
					result.add(pair);
				} else {
					result.add(description);
				}
			}
		}
		return result;
	}

	public static void main(String[] args) throws JsonProcessingException {
		System.out.println(getBeanDescription(new JobBean()));
		System.out.println(JsonManager.json(getBeanDescription(new JobBean(),
				JsonType.Map)));
	}

	public static String fontColor(String value, String color) {
		return "<font color='" + color + "'>" + value + "</font>";
	}
}
