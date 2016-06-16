package org.xserver.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
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
	/**
	 * get the file content
	 * 
	 * @see #getFileContent(File, String)
	 * @param filePath
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String getFileContent(String filePath) throws IOException {
		return getFileContent(new File(filePath));
	}

	/**
	 * get the file content
	 * 
	 * @see #getFileContent(File, String)
	 * @param filePath
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String getFileContent(File file) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

		ReadableByteChannel readChannel = Channels.newChannel(new FileInputStream(file));
		WritableByteChannel writeChannel = Channels.newChannel(outputStream);

		try {
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

			return outputStream.toString(getFileEncoding(file));
		} finally {
			readChannel.close();
			writeChannel.close();
		}
	}

	public static String getFileContent2(String filePath) throws IOException {
		return getFileContent2(new File(filePath));
	}

	public static String getFileContent2(File file) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
		FileChannel channel = inputStream.getChannel();

		try {
			channel.transferTo(0, inputStream.available(), Channels.newChannel(outputStream));
			return outputStream.toString(getFileEncoding(file));
		} finally {
			inputStream.close();
			outputStream.close();
			channel.close();
		}
	}

	public static String getFileEncoding(String filePath) throws IOException {
		FileWriter writer = new FileWriter(filePath);
		try {
			return writer.getEncoding();
		} finally {
			writer.close();
		}
	}

	public static String getFileEncoding(File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			return writer.getEncoding();
		} finally {
			writer.close();
		}
	}
}
