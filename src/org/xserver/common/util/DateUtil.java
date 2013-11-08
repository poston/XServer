package org.xserver.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.elasticsearch.common.joda.time.Hours;

public class DateUtil {
	public static final String DF_yyyyMMddHHmmss = "yyyyMMddHHmmss";
	public static final String DF_yyMMddHHmmss = "yyMMddHHmmss";
	public static final String DF_yyyyMMdd = "yyyyMMdd";
	public static final String DF_yyyyMMddHH = "yyyyMMddHH";
	public static final String DF_yyyyMM = "yyyyMM";
	public static final String DF_yyMMdd = "yyMMdd";
	public static final String DF_yyyy_MM = "yyyy-MM";
	public static final String DF_yyyy_MM_dd_HHmmss = "yyyy-MM-dd HH:mm:ss";
	public static final String DF_yy_MM_dd_HHmmss = "yy-MM-dd HH:mm:ss";
	public static final String DF_yyyy_MM_dd = "yyyy-MM-dd";
	public static final String DF_yy_MM_dd = "yy-MM-dd";
	public static final String DF_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	public static final String DF_DEFAULT_DAY = "yyyy-MM-dd";
	public static final String DF_DEFAULT_GMT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	public static final DateFormat UNSAFE_DF_yyyyMMddHHmmss = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	public static final DateFormat UNSAFE_DF_yyMMddHHmmss = new SimpleDateFormat(
			"yyMMddHHmmss");
	public static final DateFormat UNSAFE_DF_yyyyMMdd = new SimpleDateFormat(
			"yyyyMMdd");
	public static final DateFormat UNSAFE_DF_yyMMdd = new SimpleDateFormat(
			"yyMMdd");
	public static final DateFormat UNSAFE_DF_yyyy_MM_dd_HHmmss = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static final DateFormat UNSAFE_DF_yy_MM_dd_HHmmss = new SimpleDateFormat(
			"yy-MM-dd HH:mm:ss");
	public static final DateFormat UNSAFE_DF_yyyy_MM_dd = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final DateFormat UNSAFE_DF_yy_MM_dd = new SimpleDateFormat(
			"yy-MM-dd");
	public static final DateFormat UNSAFE_DF_DEFAULT = UNSAFE_DF_yyyy_MM_dd_HHmmss;
	public static final DateFormat UNSAFE_DF_DEFAULT_DAY = UNSAFE_DF_yyyy_MM_dd;

	public static final ThreadLocal<DateFormat> SAFE_DF_DEFAULT = makeDateFormatPerThread(DF_yyyy_MM_dd_HHmmss);
	public static final ThreadLocal<DateFormat> SAFE_DF_DEFAULT_DAY = makeDateFormatPerThread(DF_yyyy_MM_dd);
	public static final ThreadLocal<DateFormat> SAFE_DF_DEFAULT_GMT = makeDateFormatPerThread(DF_DEFAULT_GMT);

	public static ThreadLocal<DateFormat> makeDateFormatPerThread(
			final String pattern) {
		return new ThreadLocal<DateFormat>() {
			protected synchronized DateFormat initialValue() {
				return new SimpleDateFormat(pattern);
			}
		};
	}

	/**
	 * Set calendar
	 * 
	 * @param calendar
	 *            original object
	 * @param calendarField
	 *            TimeUnit
	 * @param amount
	 *            setting value
	 * @return Calendar
	 */
	public static Calendar set(Calendar calendar, int calendarField, int amount) {
		calendar.set(calendarField, amount);
		return calendar;
	}

	/**
	 * Set calendar
	 * 
	 * @param data
	 *            original object
	 * @param calendarField
	 *            time unit
	 * @param amount
	 *            setting value
	 * @return Date
	 */
	public static Date set(Date date, int calendarField, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendarField, amount);
		return calendar.getTime();
	}

	/**
	 * Set calendar
	 * 
	 * @param timeMillis
	 *            the time in milliseconds
	 * @param calendarField
	 *            time unit
	 * @param amount
	 *            setting value
	 * @return milliseconds
	 */
	public static long set(long timeMillis, int calendarField, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		calendar.set(calendarField, amount);
		return calendar.getTimeInMillis();
	}

	/**
	 * Add calendar
	 * 
	 * @param calendar
	 *            original object
	 * @param calendarField
	 *            time unit
	 * @param amount
	 *            adding value
	 * @return Calendar
	 */
	public static Calendar add(Calendar calendar, int calendarField, int amount) {
		calendar.add(calendarField, amount);
		return calendar;
	}

	/**
	 * Add calendar
	 * 
	 * @param date
	 *            original object
	 * @param calendarField
	 *            time unit
	 * @param amount
	 *            adding value
	 * @return Date
	 */
	public static Date add(Date date, int calendarField, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(calendarField, amount);
		return calendar.getTime();
	}

	/**
	 * Add calendar
	 * 
	 * @param timeMillis
	 *            the time in milliseconds
	 * @param calendarField
	 *            time unit
	 * @param amount
	 *            adding value
	 * @return milliseconds
	 */
	public static long add(long timeMillis, int calendarField, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		calendar.add(calendarField, amount);
		return calendar.getTimeInMillis();
	}

	/**
	 * Add calendar
	 * 
	 * @param dateStr
	 *            original date string
	 * @param pattern
	 *            original date pattern
	 * @param calendarField
	 *            time unit
	 * @param amount
	 *            adding value
	 * @return date string
	 */
	public static String add(String dateStr, String pattern, int calendarField,
			int amount) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			Date date = sdf.parse(dateStr);
			Date newDate = add(date, calendarField, amount);
			return sdf.format(newDate);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Roll calendar
	 * 
	 * @param calendar
	 *            original object
	 * @param calendarField
	 *            time unit
	 * @param amount
	 *            adding value
	 * @return Calendar
	 */
	public static Calendar roll(Calendar calendar, int calendarField, int amount) {
		calendar.roll(calendarField, amount);
		return calendar;
	}

	/**
	 * Roll calendar
	 * 
	 * @param date
	 *            original object
	 * @param calendarField
	 *            time unit
	 * @param amount
	 *            adding value
	 * @return Date
	 */
	public static Date roll(Date date, int calendarField, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.roll(calendarField, amount);
		return calendar.getTime();
	}

	/**
	 * Roll calendar
	 * 
	 * @param timeMillis
	 *            original object
	 * @param calendarField
	 *            time unit
	 * @param amount
	 *            adding value
	 * @return Date
	 */
	public static long roll(long timeMillis, int calendarField, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		calendar.roll(calendarField, amount);
		return calendar.getTimeInMillis();
	}

	public static Calendar ceil(Calendar calendar, int calendarField) {
		DateUtils.modify(calendar, calendarField, 2);
		return calendar;
	}

	public static Date ceil(Date date, int calendarField) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		DateUtils.modify(calendar, calendarField, 2);
		return calendar.getTime();
	}

	public static long ceil(long timeMillis, int calendarField) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		DateUtils.modify(calendar, calendarField, 2);
		return calendar.getTimeInMillis();
	}

	public static Calendar round(Calendar calendar, int calendarField) {
		DateUtils.modify(calendar, calendarField, 1);
		return calendar;
	}

	public static Date round(Date date, int calendarField) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		DateUtils.modify(calendar, calendarField, 1);
		return calendar.getTime();
	}

	public static long round(long timeMillis, int calendarField) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		DateUtils.modify(calendar, calendarField, 1);
		return calendar.getTimeInMillis();
	}

	public static Calendar truncate(Calendar calendar, int calendarField) {
		DateUtils.modify(calendar, calendarField, 0);
		return calendar;
	}

	public static Date truncate(Date date, int calendarField) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		DateUtils.modify(calendar, calendarField, 0);
		return calendar.getTime();
	}

	public static long truncate(long timeMillis, int calendarField) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		DateUtils.modify(calendar, calendarField, 0);
		return calendar.getTimeInMillis();
	}

	public static Calendar oper(Calendar calendar, long[] opers) {
		for (long operMagic : opers) {
			int amount = DateUtilHelper.getAmount(operMagic);
			int operType = DateUtilHelper.getOperType(operMagic);
			int calendarField = DateUtilHelper.getCalendarField(operMagic);
			switch (operType) {
			case DateUtilHelper.SET:
				calendar.set(calendarField, amount);
				break;
			case DateUtilHelper.ADD:
				calendar.add(calendarField, amount);
				break;
			case DateUtilHelper.ROLL:
				calendar.roll(calendarField, amount);
				break;
			case DateUtilHelper.CEIL:
				DateUtils.modify(calendar, calendarField, operType);
				break;
			case DateUtilHelper.ROUND:
				DateUtils.modify(calendar, calendarField, operType);
				break;
			case DateUtilHelper.TRUNCATE:
				DateUtils.modify(calendar, calendarField, operType);
			}
		}

		return calendar;
	}

	public static Date oper(Date date, long[] opers) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Calendar r = oper(calendar, opers);
		return r.getTime();
	}

	public static long oper(long timeMillis, long[] opers) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMillis);
		Calendar r = oper(calendar, opers);
		return r.getTimeInMillis();
	}

	public static long getInterval(Date date1, Date date2,
			UnitConverter.TimeUnit timeUnit, boolean ceil) {
		if (timeUnit == UnitConverter.TimeUnit.month) {
			return getMonthsBetween(date1, date2, ceil);
		}
		if (timeUnit == UnitConverter.TimeUnit.year) {
			return getYearsBetween(date1, date2, ceil);
		}
		return (long) UnitConverter.convertTime(
				date1.getTime() - date2.getTime(),
				UnitConverter.TimeUnit.millisecond, timeUnit, ceil);
	}

	public static long getInterval(Date date1, Date date2,
			UnitConverter.TimeUnit timeUnit) {
		return getInterval(date1, date2, timeUnit, false);
	}

	private static long getYearsBetween(Date date1, Date date2, boolean ceil) {
		Calendar c = Calendar.getInstance();
		c.setTime(date1);
		int year1 = c.get(1);
		c.set(1, 1986);
		long time1 = c.getTimeInMillis();

		c.setTime(date2);
		int year2 = c.get(1);
		c.set(1, 1986);
		long time2 = c.getTimeInMillis();

		long result = year1 - year2;
		return ceil(result, time1, time2, ceil);
	}

	private static long getMonthsBetween(Date date1, Date date2, boolean ceil) {
		Calendar c = Calendar.getInstance();
		c.setTime(date1);
		int month1 = c.get(2);
		int year1 = c.get(1);
		c.set(2, 0);
		c.set(1, 1986);
		long time1 = c.getTimeInMillis();

		c.setTime(date2);
		int month2 = c.get(2);
		int year2 = c.get(1);
		c.set(2, 0);
		c.set(1, 1986);
		long time2 = c.getTimeInMillis();

		long result = (year1 - year2) * 12L + (month1 - month2);
		return ceil(result, time1, time2, ceil);
	}

	private static long ceil(long result, long time1, long time2, boolean ceil) {
		long ret = result;
		if (ceil) {
			if (ret == 0L) {
				long diff = time1 - time2;
				if (diff > 0L)
					ret = 1L;
				else if (diff < 0L) {
					ret = -1L;
				}
			}

		} else if (ret != 0L) {
			long diff = time1 - time2;
			if (diff != 0L) {
				if ((ret > 0L) && (diff < 0L))
					ret -= 1L;
				else if ((ret < 0L) && (diff > 0L)) {
					ret += 1L;
				}
			}
		}

		return ret;
	}

	public static Date now() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	public static String now(String pattern) {
		Date now = now();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(now);
	}

	public static String today(String pattern) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static String tomorrow(String pattern) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static String tomorrow0(String pattern) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static String yesterday(String pattern) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static String yesterday0(String pattern) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static Date parser(String dateStr, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(dateStr);
		} catch (Exception e) {
			return null;
		}
	}

	public static String format(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static int weekInMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
	}

	public static int weekInMonth(String date, String pattern)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date _date = sdf.parse(date);
		return weekInMonth(_date);
	}

	public static String format(String source, String sourcePattern,
			String destinationPattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(sourcePattern);
		try {
			Date sourceDate = sdf.parse(source);
			sdf.applyPattern(destinationPattern);
			return sdf.format(sourceDate);
		} catch (ParseException e) {
			return null;
		}
	}

	public static void main(String[] args) throws ParseException {
		System.out.println(weekInMonth("2013-10-30", "yyyy-MM-dd"));
	}
}
