package org.xserver.common.util;

public class UnitConverter {
	private static double convert(long from, long to, boolean ceil) {
		if (ceil) {
			return ceil(from / to);
		}
		return from / to;
	}

	private static double ceil(double num) {
		if (num < 0.0D) {
			return -Math.ceil(-num);
		}
		return Math.ceil(num);
	}

	public static long convertByte(long fromValue, ByteUnit fromUnit,
			ByteUnit toUnit) {
		return (long) convertByte(fromValue, fromUnit, toUnit, false);
	}

	public static long convertByteCeil(long fromValue, ByteUnit fromUnit,
			ByteUnit toUnit) {
		return (long) convertByte(fromValue, fromUnit, toUnit, true);
	}

	public static double convertByte(long fromValue, ByteUnit fromUnit,
			ByteUnit toUnit, boolean ceil) {
		return convert(fromValue * fromUnit.get(), toUnit.get(), ceil);
	}

	public static long convertTime(long fromValue, TimeUnit fromUnit,
			TimeUnit toUnit) {
		return (long) convertTime(fromValue, fromUnit, toUnit, false);
	}

	public static long convertTimeCeil(long fromValue, TimeUnit fromUnit,
			TimeUnit toUnit) {
		return (long) convertTime(fromValue, fromUnit, toUnit, true);
	}

	public static double convertTime(long fromValue, TimeUnit fromUnit,
			TimeUnit toUnit, boolean ceil) {
		return convert(fromValue * fromUnit.get(), toUnit.get(), ceil);
	}

	public static long convertRmb(long fromValue, RmbUnit fromUnit,
			RmbUnit toUnit) {
		return (long) convertRmb(fromValue, fromUnit, toUnit, false);
	}

	public static long convertTimeCeil(long fromValue, RmbUnit fromUnit,
			RmbUnit toUnit) {
		return (long) convertRmb(fromValue, fromUnit, toUnit, true);
	}

	public static double convertRmb(long fromValue, RmbUnit fromUnit,
			RmbUnit toUnit, boolean ceil) {
		return convert(fromValue * fromUnit.get(), toUnit.get(), ceil);
	}

	public static enum ByteUnit {
		b(1L), kb(1024L), mb(1048576L), gb(1073741824L), tb(1099511627776L);

		private long value;

		private ByteUnit(long value) {
			this.value = value;
		}

		public long get() {
			return this.value;
		}
	}

	public static enum RmbUnit {
		fen(1L), jiao(10L), yuan(100L);

		private long value;

		private RmbUnit(long value) {
			this.value = value;
		}

		public long get() {
			return this.value;
		}
	}

	public static enum TimeUnit {
		millisecond(1L), second(1000L), minute(60000L), hour(3600000L), day(
				86400000L), week(604800000L), month31(2678400000L), month30(
				2592000000L), year365(31536000000L),

		month(2592000000L), year(31536000000L);

		private long value;

		private TimeUnit(long value) {
			this.value = value;
		}

		public long get() {
			return this.value;
		}
	}

	public static void main(String[] args) {
		System.out.println(convertTime(2L, TimeUnit.day, TimeUnit.millisecond));
		System.out.println(convertTime(1L, TimeUnit.year, TimeUnit.day));
		System.out.println(convertTimeCeil(1L, TimeUnit.day, TimeUnit.year));
		System.out.println(convertByte(1L, ByteUnit.kb, ByteUnit.b));
		System.out.println(Math.ceil(-3.2D));
	}
}
