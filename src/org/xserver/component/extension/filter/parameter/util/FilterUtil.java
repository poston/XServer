package org.xserver.component.extension.filter.parameter.util;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.json.JsonManager;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterUtil {

	private static final Logger log = LoggerFactory.getLogger(FilterUtil.class);
	private static Map<Class<?>, ParameterValueGetter> simpleClassMap = initSimpleClass();

	/**
	 * Initialize simpleClassMap, it contain Boolean(boolean), Byte(byte),
	 * Short(short), Integer(int), Float(float), Double(double), Long(long) and
	 * String eight basic types.
	 * 
	 * @return
	 */
	private static Map<Class<?>, ParameterValueGetter> initSimpleClass() {
		Map<Class<?>, ParameterValueGetter> temp = new HashMap<Class<?>, ParameterValueGetter>();

		ParameterValueGetter parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				HttpMethod httpMethod = request.getMethod();

				if (httpMethod.equals(HttpMethod.GET)) {
					return request.getParameterInt(key, 0);
				}

				if (httpMethod.equals(HttpMethod.POST)
						|| httpMethod.equals(HttpMethod.PUT)) {
					return request.getParameterIntegerByPost(key, 0);
				}
				
				return null;
			}
		};
		temp.put(int.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				try {
					HttpMethod httpMethod = request.getMethod();

					if (httpMethod.equals(HttpMethod.GET)) {
						return request.getParameterInteger(key);
					}

					if (httpMethod.equals(HttpMethod.POST)
							|| httpMethod.equals(HttpMethod.PUT)) {
						return request.getParameterIntegerByPost(key);
					}
					
					return null;
				} catch (Exception e) {
					return null;
				}
			}
		};
		temp.put(Integer.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				HttpMethod httpMethod = request.getMethod();

				if (httpMethod.equals(HttpMethod.GET)) {
					return request.getParameterLong(key, 0L);
				}

				if (httpMethod.equals(HttpMethod.POST)
						|| httpMethod.equals(HttpMethod.PUT)) {
					return request.getParameterLongByPost(key, 0L);
				}
				
				return null;
			}
		};
		temp.put(long.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				try {
					HttpMethod httpMethod = request.getMethod();

					if (httpMethod.equals(HttpMethod.GET)) {
						return request.getParameterLong(key);
					}

					if (httpMethod.equals(HttpMethod.POST)
							|| httpMethod.equals(HttpMethod.PUT)) {
						return request.getParameterLongByPost(key);
					}
					
					return null;
				} catch (Exception e) {
					return null;
				}
			}
		};
		temp.put(Long.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				HttpMethod httpMethod = request.getMethod();

				if (httpMethod.equals(HttpMethod.GET)) {
					return request.getParameterFloat(key, 0f);
				}

				if (httpMethod.equals(HttpMethod.POST)
						|| httpMethod.equals(HttpMethod.PUT)) {
					return request.getParameterFloatByPost(key, 0f);
				}
				
				return null;
			}
		};
		temp.put(float.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				try {
					HttpMethod httpMethod = request.getMethod();

					if (httpMethod.equals(HttpMethod.GET)) {
						return request.getParameterFloat(key, 0f);
					}

					if (httpMethod.equals(HttpMethod.POST)
							|| httpMethod.equals(HttpMethod.PUT)) {
						return request.getParameterFloatByPost(key, 0f);
					}

					return null;
				} catch (Exception e) {
					return null;
				}
			}
		};
		temp.put(Float.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				HttpMethod httpMethod = request.getMethod();

				if (httpMethod.equals(HttpMethod.GET)) {
					return request.getParameterDouble(key, 0.0);
				}

				if (httpMethod.equals(HttpMethod.POST)
						|| httpMethod.equals(HttpMethod.PUT)) {
					return request.getParameterDoubleByPost(key, 0.0);
				}

				return null;
			}
		};
		temp.put(double.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				try {
					HttpMethod httpMethod = request.getMethod();

					if (httpMethod.equals(HttpMethod.GET)) {
						return request.getParameterDouble(key);
					}

					if (httpMethod.equals(HttpMethod.POST)
							|| httpMethod.equals(HttpMethod.PUT)) {
						return request.getParameterDoubleByPost(key);
					}
					return null;
				} catch (Exception e) {
					return null;
				}
			}
		};
		temp.put(Double.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				HttpMethod httpMethod = request.getMethod();

				if (httpMethod.equals(HttpMethod.GET)) {
					return request.getParameterByte(key, (byte) 0);
				}

				if (httpMethod.equals(HttpMethod.POST)
						|| httpMethod.equals(HttpMethod.PUT)) {
					return request.getParameterByteByPost(key, (byte) 0);
				}

				return null;
			}
		};
		temp.put(byte.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				try {
					HttpMethod httpMethod = request.getMethod();

					if (httpMethod.equals(HttpMethod.GET)) {
						return request.getParameterShort(key);
					}

					if (httpMethod.equals(HttpMethod.POST)
							|| httpMethod.equals(HttpMethod.PUT)) {
						return request.getParameterByteByPost(key);
					}

					return null;
				} catch (Exception e) {
					return null;
				}
			}
		};
		temp.put(Byte.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				HttpMethod httpMethod = request.getMethod();

				if (httpMethod.equals(HttpMethod.GET)) {
					return request.getParameterShort(key, (short) 0);
				}

				if (httpMethod.equals(HttpMethod.POST)
						|| httpMethod.equals(HttpMethod.PUT)) {
					return request.getParameterShortByPost(key, (short) 0);
				}

				return null;
			}
		};
		temp.put(short.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				try {
					HttpMethod httpMethod = request.getMethod();

					if (httpMethod.equals(HttpMethod.GET)) {
						return request.getParameterShort(key);
					}

					if (httpMethod.equals(HttpMethod.POST)
							|| httpMethod.equals(HttpMethod.PUT)) {
						return request.getParameterShortByPost(key);
					}

					return null;
				} catch (Exception e) {
					return null;
				}
			}
		};
		temp.put(Short.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				HttpMethod httpMethod = request.getMethod();

				if (httpMethod.equals(HttpMethod.GET)) {
					return request.getParameterBoolean(key, false);
				}

				if (httpMethod.equals(HttpMethod.POST)
						|| httpMethod.equals(HttpMethod.PUT)) {
					return request.getParameterBooleanByPost(key, false);
				}

				return null;
			}
		};
		temp.put(boolean.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				try {
					HttpMethod httpMethod = request.getMethod();

					if (httpMethod.equals(HttpMethod.GET)) {
						return request.getParameterBoolean(key);
					}

					if (httpMethod.equals(HttpMethod.POST)
							|| httpMethod.equals(HttpMethod.PUT)) {
						return request.getParameterBooleanByPost(key);
					}

					return null;
				} catch (Exception e) {
					return null;
				}
			}
		};
		temp.put(Boolean.class, parameterValueGetter);

		parameterValueGetter = new ParameterValueGetter() {

			@Override
			public Object getValue(XServerHttpRequest request, Class<?> clazz,
					String key) {
				HttpMethod httpMethod = request.getMethod();

				if (httpMethod.equals(HttpMethod.GET)) {
					return request.getParameter(key);
				}

				if (httpMethod.equals(HttpMethod.POST)
						|| httpMethod.equals(HttpMethod.PUT)) {
					return request.getParameterByPost(key);
				}

				return null;
			}
		};
		temp.put(String.class, parameterValueGetter);

		return temp;
	}

	public static List<Object> getParameterValues(
			XServerHttpContextAttachment attachment, String[] keys,
			Class<?>[] classes) throws Exception {
		if (keys.length != classes.length) {
			throw new InvalidParameterException(
					"keys's length must equals classes's length.");
		}

		List<Object> objects = new ArrayList<Object>();

		for (int i = 0; i < classes.length; i++) {
			// 1. build in objects
			if (buildInParameters(attachment, classes[i], objects)) {
				continue;
			}

			// 2. convert parameter for simple class
			if (simpleParameters(attachment, classes[i], keys[i], objects)) {
				continue;
			}

			// 3. convert parameter for composite object
			compositeParameters(attachment, classes[i], keys[i], objects);
		}

		return objects;
	}

	private static <T> T getParameterBeanFromJson(XServerHttpRequest request,
												  String key, Class<T> clazz) {
		String json = null;
		HttpMethod httpMethod = request.getMethod();
		if (httpMethod.equals(HttpMethod.GET)) {
			json = request.getParameter(key);
		}

		if (httpMethod.equals(HttpMethod.POST)
				|| httpMethod.equals(HttpMethod.PUT)) {
			json = request.getParameterByPost(key);
		}

		return JsonManager.getBean(json, clazz);
	}

	/**
	 * 
	 * @param request
	 *            http request
	 * @param clazz
	 *            parameter type
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static <T> T getParameterBean(XServerHttpRequest request,
										  String key, Class<T> clazz) throws Exception {
		return getParameterBeanFromJson(request, key, clazz);
//		if (null == t) {
//			t = clazz.newInstance();
//		}
//		// TODO 测试这个方式是否可以直接取到父类的属性 --20150921--
//		// PropertyDescriptor[] sDescriptors =
//		// Introspector.getBeanInfo(clazz).getPropertyDescriptors();
//
//		Field[] fields = clazz.getDeclaredFields();
//		for (Field field : fields) {
//			if (ReflectionUtil.isStatic(field)) {
//				continue;
//			}
//
//			ParameterValueGetter parameterValueGetter = simpleClassMap
//					.get(field.getType());
//
//			Object value = null;
//			if (parameterValueGetter != null) {
//				value = parameterValueGetter.getValue(request, field.getType(),
//						field.getName());
//			}
//			field.setAccessible(true);
//			field.set(t, value);
//		}
//		/*
//		 * if T has super class, set value of super class field to T
//		 */
//		Class<? super T> superClass = clazz.getSuperclass();
//		if (null != superClass) {
//			getParameterBean(request, superClass, t);
//		}
//		return t;
	}

	/*
	 * private static <T> T getParameterBeanFromJson(XServerHttpRequest request,
	 * String key, Class<T> clazz) { String json = null; HttpMethod httpMethod =
	 * request.getMethod(); if (httpMethod.equals(HttpMethod.GET)) { json =
	 * request.getParameter(key); }
	 * 
	 * if (httpMethod.equals(HttpMethod.POST) ||
	 * httpMethod.equals(HttpMethod.PUT)) { json =
	 * request.getParameterByPost(key); }
	 * 
	 * return JsonManager.getBean(json, clazz); }
	 */

	/**
	 * If the interface method parameter is build in parameter, just add build
	 * in parameter
	 * 
	 * @param attachment
	 * @param clazz
	 * @param objects
	 * @return
	 */
	private static boolean buildInParameters(
			XServerHttpContextAttachment attachment, Class<?> clazz,
			List<Object> objects) {
		if (XServerHttpRequest.class.isAssignableFrom(clazz)) {
			objects.add(attachment.getRequest());
			return true;
		}

		if (XServerHttpResponse.class.isAssignableFrom(clazz)) {
			objects.add(attachment.getResponse());
			return true;
		}

		return false;
	}

	/**
	 * If the interface method parameter is is simple parameter, convert
	 * parameter and add converted value to {@code objects}
	 * 
	 * @param attachment
	 * @param clazz
	 * @param key
	 * @param objects
	 * @return
	 * @throws Exception
	 */
	private static boolean simpleParameters(
			XServerHttpContextAttachment attachment, Class<?> clazz,
			String key, List<Object> objects) throws Exception {
		ParameterValueGetter parameterValueGetter = simpleClassMap.get(clazz);

		if (parameterValueGetter == null) {
			return false;
		}

		objects.add(parameterValueGetter.getValue(attachment.getRequest(),
				clazz, key));
		return true;
	}

	/**
	 * If the interface method parameter is composite object, convert the http
	 * request parameters to composite object
	 * 
	 * @param attachment
	 * @param clazz
	 * @param objects
	 * @throws Exception
	 */
	/*private static void compositeParameters(
			XServerHttpContextAttachment attachment, Class<?> clazz,
			String key, List<Object> objects) throws Exception {
		objects.add(getParameterBean(attachment.getRequest(), clazz, key));
	}*/
	
	/**
	 * If the interface method parameter is composite object, convert the http
	 * request parameters to composite object
	 * 
	 * @param attachment
	 * @param clazz
	 * @param objects
	 * @throws Exception
	 */
	private static void compositeParameters(
			XServerHttpContextAttachment attachment, Class<?> clazz,
			String key, List<Object> objects) throws Exception {
		objects.add(getParameterBean(attachment.getRequest(), key, clazz));
	}
}
