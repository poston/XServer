package org.xserver.common.util;

import java.nio.charset.Charset;

/**
 * Responsible for Encode and Decode work
 * 
 * @author postonzhang
 * @since 2013/01/08
 */
public class CharsetUtil {
	public static final Charset GBK = Charset.forName("GBK");
	public static final Charset GB2312 = Charset.forName("GB2312");
	public static final Charset GB18030 = Charset.forName("GB18030");
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	public static final Charset UTF_16 = Charset.forName("UTF_16");
	public static final Charset UTF_16BE = Charset.forName("UTF_16BE");
	public static final Charset UTF_16LE = Charset.forName("UTF_16LE");
	public static final Charset US_ASCII = Charset.forName("US-ASCII");
	public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

	public static String newString(byte[] bytes, Charset charset) {
		if (bytes == null) {
			return null;
		}

		return new String(bytes, charset);
	}

	public static byte[] getBytes(String content, Charset charset) {
		return content.getBytes(charset);
	}
}
