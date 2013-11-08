package org.xserver.common.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xserver.component.annotation.Alias;

public class ReflectionUtil {

	// private static final Logger log = LoggerFactory
	// .getLogger(ReflectionUtil.class);

	/**
	 * Whether found class
	 * 
	 * @param className
	 *            target class name
	 * @return if found return true, else return false
	 */
	public static boolean classFound(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (Exception e) {
		}

		return false;
	}

	/**
	 * Whether found classes
	 * 
	 * @param classNames
	 *            target classes names
	 * @return if all classes found return true, else return false
	 */
	public static boolean classFound(String[] classNames) {
		try {
			if (!StringUtil.isEmpty(classNames)) {
				for (String className : classNames) {
					classFound(className);
				}
			}
			return true;
		} catch (Exception e) {
		}

		return false;
	}

	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get the specified class field as Map return
	 * 
	 * @param clazz
	 *            the specified class
	 * @return the field map
	 */
	public static <T> Map<String, Field> getFieldMap(Class<T> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		Map<String, Field> fieldMap = new HashMap<String, Field>(fields.length);
		for (Field field : fields) {
			Alias alias = field.getAnnotation(Alias.class);
			String name = alias == null ? field.getName() : alias.value();
			fieldMap.put(name, field);
		}

		return fieldMap;
	}

	public static Class<?> getGenericClass(Field field) {
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			return (Class<?>) pt.getActualTypeArguments()[0];
		}

		return field.getType();
	}

	/**
	 * Exclude specified fields and return other fields in class, but when the
	 * exceptFields is empty or null just return a string array with zero length
	 * 
	 * @param clazz
	 * @param exceptFields
	 * @return
	 */
	public static String[] exceptFields(Class<?> clazz, String[] exceptFields) {
		if (StringUtil.isEmpty(exceptFields)) {
			return new String[] {};
		}

		Field[] fields = clazz.getDeclaredFields();

		Map<String, String> map = new HashMap<String, String>();
		for (Field field : fields) {
			map.put(field.getName(), field.getName());
		}

		for (String exceptField : exceptFields) {
			if (map.containsKey(exceptField)) {
				map.remove(exceptField);
			}
		}

		String[] includeFields = (String[]) Array.newInstance(String.class, map
				.keySet().size());
		map.keySet().toArray(includeFields);
		return includeFields;
	}

	public static Class<?>[] getClasses(String[] classes)
			throws ClassNotFoundException {
		if (!StringUtil.isEmpty(classes)) {
			List<Class<?>> list = new ArrayList<Class<?>>();
			Class<?>[] result = new Class<?>[classes.length];
			for (String clazz : classes) {
				list.add(Class.forName(clazz));
			}

			list.toArray(result);
			return result;
		}

		return null;
	}

	public static void main(String[] args) throws ClassNotFoundException {
		Class<?>[] classes = getClasses(new String[] {
				DateUtil.class.getName(), CharsetUtil.class.getName() });
		for (Class clazz : classes) {
			System.out.println(clazz.getName());
		}
	}
}
