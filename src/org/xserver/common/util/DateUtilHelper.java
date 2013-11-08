package org.xserver.common.util;

public class DateUtilHelper {
	/** OPERATION: SET */
	public static final int SET = 5;
	/** OPERATION: ADD */
	public static final int ADD = 4;
	/** OPERATION: ROLL */
	public static final int ROLL = 3;
	/** OPERATION: CEIL */
	public static final int CEIL = 2;
	/** OPERATION: ROUND */
	public static final int ROUND = 1;
	/** OPERATION: TRUNCATE */
	public static final int TRUNCATE = 0;

	public static long set(int calendarField, int amount) {
		if (amount < 0) {
			return amount * 1000L - 500L - calendarField;
		}
		return amount * 1000L + 500L + calendarField;
	}

	public static long add(int calendarField, int amount) {
		if (amount < 0) {
			return amount * 1000L - 400L - calendarField;
		}
		return amount * 1000L + 400L + calendarField;
	}

	public static long roll(int calendarField, int amount) {
		if (amount < 0) {
			return amount * 1000L - 300L - calendarField;
		}
		return amount * 1000L + 300L + calendarField;
	}

	public static long ceil(int calendarField) {
		return 200 + calendarField;
	}

	public static long round(int calendarField) {
		return 100 + calendarField;
	}

	public static long truncate(int calendarField) {
		return 0 + calendarField;
	}

	public static int getAmount(long operMagic) {
		return (int) (operMagic / 1000L);
	}

	public static int getOperType(long operMagic) {
		int r = (int) (operMagic % 1000L / 100L);
		return Math.abs(r);
	}

	public static int getCalendarField(long operMagic) {
		int r = (int) (operMagic % 100L);
		return Math.abs(r);
	}
}