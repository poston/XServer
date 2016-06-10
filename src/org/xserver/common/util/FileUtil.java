package org.xserver.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * File operation util
 * 
 * @author postonzhang
 * @since 2016/06/10
 *
 */
public class FileUtil {
	public static final String DEFAULT_FILE_CHARSET = "UTF-8";

	/**
	 * get the file content, TODO auto get file charset
	 * 
	 * @see #getFileContent(File, String)
	 * @param filePath
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String getFileContent(String filePath, String charset) throws IOException {
		return getFileContent(new File(filePath), charset);
	}

	/**
	 * get the file content, TODO auto get file charset
	 * 
	 * @see #getFileContent(File, String)
	 * @param filePath
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String getFileContent(String filePath) throws IOException {
		return getFileContent(new File(filePath), DEFAULT_FILE_CHARSET);
	}

	/**
	 * get the file content, TODO auto get file charset
	 * 
	 * @see #getFileContent(File, String)
	 * @param filePath
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String getFileContent(File file) throws IOException {
		return getFileContent(file, DEFAULT_FILE_CHARSET);
	}

	public static String getFileContent(File file, String charset) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

		ReadableByteChannel readChannel = Channels.newChannel(new FileInputStream(file));
		WritableByteChannel writeChannel = Channels.newChannel(outputStream);

		ByteBuffer buffer = ByteBuffer.allocate(1024);
		while (readChannel.read(buffer) != -1) {
			buffer.flip();

			writeChannel.write(buffer);

			buffer.compact();
		}

		buffer.flip();

		while (buffer.hasRemaining()) {
			writeChannel.write(buffer);
		}

		return outputStream.toString(charset);

	}
}
