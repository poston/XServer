package org.xserver.common.util;

import java.lang.reflect.Array;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

	/**
	 * the true if this content is null or does not have the any character
	 */
	public static boolean isEmpty(Object value) {
		if (value == null || value.equals("null")) {
			return true;
		}

		if (value instanceof String) {
			if (((String) value).length() == 0) {
				return true;
			}

			return false;
		} else if (value.getClass().isArray()) {
			if (Array.getLength(value) == 0) {
				return true;
			}
		}

		return false;
	}

	public static String toLowerCaseAtIndex(String content, int index) {
		if (isEmpty(content)) {
			throw new IllegalArgumentException(
					"Argument cannot be null or empty.");
		}

		if (index < 0 || index >= content.length()) {
			throw new IllegalArgumentException(
					"Index range should at [0, content.length())");
		}

		return content.substring(0, index)
				+ String.valueOf(Character.toLowerCase(content.charAt(index)))
				+ content.substring(index + 1);
	}

	public static String toUpperCaseAtIndex(String content, int index) {
		if (isEmpty(content)) {
			throw new IllegalArgumentException(
					"Argument cannot be null or empty.");
		}

		if (index < 0 || index >= content.length()) {
			throw new IllegalArgumentException(
					"Index range should at [0, content.length())");
		}

		return content.substring(0, index)
				+ String.valueOf(Character.toUpperCase(content.charAt(index)))
				+ content.substring(index + 1);
	}

	public static String templateReplace(String template, String split,
			String[] args) {
		if (isEmpty(template) || isEmpty(split) || isEmpty(args)) {
			throw new IllegalArgumentException(
					"Arguments should contail contents");
		}

		if (split.equals("|")) {
			split = "\\|";
		}
		String[] templateArray = template.split(split);
		int templateArrayLength = templateArray.length;
		if (template.endsWith(split)) {
			templateArrayLength++;
		}
		if (templateArrayLength - 1 != args.length) {
			throw new IllegalArgumentException("Args cannot match template");
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < args.length; i++) {
			sb.append(templateArray[i]).append(args[i]);
		}
		if (!template.endsWith(split)) {
			sb.append(templateArray[templateArray.length - 1]);
		}

		return sb.toString();
	}

	public static <T> String toString(T[] arr) {
		String result = "";
		if (!isEmpty(arr)) {
			result = StringUtils.join(arr, ",");
		}

		return result;
	}

	public static String percent(double divisor, double dividend,
			boolean percent) {
		if (dividend == 0) {
			return "NaN";
		}

		double result = divisor / dividend;
		String temp = new BigDecimal(result * 100).setScale(2,
				BigDecimal.ROUND_HALF_UP).toString();
		return percent ? temp + "%" : temp;
	}

	public static String[] addPrefix(String[] source, String prefix) {
		if (!StringUtil.isEmpty(source)) {
			String[] result = new String[source.length];
			for (int i = 0; i < source.length; i++) {
				result[i] = prefix + source[i];
			}
			return result;
		}

		return source;
	}

	public static void main(String[] args) {
		System.out.println(addPrefix(new String[] { "m_s_n", "m_r_n" },
				"xserver.")[0]);
	}
}
