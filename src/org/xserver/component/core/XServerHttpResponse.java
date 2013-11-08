package org.xserver.component.core;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;
import org.xserver.component.handler.ext.WriteHandlerManager.WriteType;

/**
 * Enum common Content-Type: Content-Type: [type]/[subtype]; parameter. See
 * http://zh.wikipedia.org/wiki/MIME. Because the HttpServer most time to
 * response json data ,so XServerHttpResponse to client default use test/plain
 * Content-Type. When the business need other type, where should be set in
 * business interface. <code>XServerHttpResponse</code> implement redirect, wrap
 * Header and so an.
 */
public class XServerHttpResponse extends DefaultHttpResponse implements
		HttpResponse {

	private static final Charset DEFAULT_CONTENT_CHARSET = CharsetUtil.UTF_8;

	private Charset contentCharset = DEFAULT_CONTENT_CHARSET;

	public static final String DEFAULT_CHARSET = "charset="
			+ DEFAULT_CONTENT_CHARSET.displayName();

	public static final HttpVersion DEFAULT_HTTP_VERSION = HttpVersion.HTTP_1_1;

	public static final HttpResponseStatus DEFAULT_HTTP_STATUS = HttpResponseStatus.OK;

	private HttpResponseStatus status;

	private int contentLength = 0;

	/** which write handler to response, default:JSON->TextPlainWriterHandler */
	private WriteType writeType = WriteType.JSON;

	/** the HTTP response head info: Content-Type, default:text/plain */
	private ContentType contentType = ContentType.TEXT_PLAIN;

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public WriteType getWriteType() {
		return writeType;
	}

	public void setWriteType(WriteType writeType) {
		this.writeType = writeType;
	}

	public Charset getContentCharset() {
		return contentCharset;
	}

	public void setContentCharset(Charset contentCharset) {
		this.contentCharset = contentCharset;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public HttpResponseStatus getStatus() {
		return status;
	}

	public void setStatus(HttpResponseStatus status) {
		this.status = status;
	}

	public XServerHttpResponse() {
		this(DEFAULT_HTTP_VERSION, DEFAULT_HTTP_STATUS);
	}

	public XServerHttpResponse(HttpVersion version, HttpResponseStatus status) {
		super(version, status);
	}

	public void setContent(ChannelBuffer content) {
		super.setContent(content);
	}

	public void setContentString(String content) {
		setContent(ChannelBuffers.copiedBuffer(content, contentCharset));
	}

	public ChannelBuffer responseBuffer(String content) {
		return responseBuffer(content, DEFAULT_CONTENT_CHARSET);
	}

	public ChannelBuffer responseBuffer(String content, Charset charset) {
		if (charset == null) {
			new IllegalArgumentException(
					"Charset should be setted before convert.");
		}

		return ChannelBuffers.copiedBuffer(content, contentCharset);
	}

	public void redirect(String locationURL) {
		setStatus(HttpResponseStatus.FOUND);
		setLocationHeader(locationURL);
	}

	public void redirect(HttpResponseStatus status, String locationURL) {
		setStatus(status);
		setLocationHeader(locationURL);
	}

	public void setContentLengthHeader(int length) {
		this.setHeader(Names.CONTENT_LENGTH, length);
	}

	public void setContentTypeHeader(String contentType) {
		this.setHeader(Names.CONTENT_TYPE, contentType);
	}

	public void setConnectionHeader(String connection) {
		this.setHeader(Names.CONNECTION, connection);
	}

	public void setLocationHeader(String location) {
		this.setHeader(Names.LOCATION, location);
	}

	public boolean setHeaderIfEmpty(String name, String value) {
		if (getHeader(name) == null) {
			setHeader(name, value);
			return true;
		}

		return false;
	}

	public enum ContentType {
		/** TYPE: TEXT */
		/** PLAIN TEXT */
		TEXT_PLAIN("text/plain"),
		/** HTML */
		TEXT_HTML("text/html"),

		/** TYPE: IMAGE and VIDEO */
		/** GIF */
		IMAGE_GIF("image/gif"),
		/** JPEG */
		IMAGE_JPEG("image/jpeg"),
		/** PNG */
		IMAGE_PNG("image/png"),
		/** MPEG */
		VIDEO_MPEG("video/mpeg"),

		/** TYPE: APPLICATION */
		/** XML */
		APPLICATION_XML("application/xhtml+xml"),
		/** OCTET-STREAM */
		APPLICATION_OCTET_STREAM("application/octet-stream"),
		/** PDF */
		APPLICATION_PDF("application/pdf"),
		/** MICROSOFT WORD */
		APPLICATION_MSWORD("application/msword"),
		/** MICROSOFT EXCEL */
		APPLICATION_MSEXCEL("application/vnd.ms-excel"),
		/** WAP 1.0 */
		APPLICATION_WAP_1_0("application/vnd.wap.xhtml+xml"),
		/** WAP 2.0 */
		APPLICATION_WAP_2_0("application/xhtml+xml"),

		/** RFC 822 */
		MESSAGE_RFC_822("message/rfc822"),
		/** HTML MAIL */
		MULTIPART_ALTERNATIVE("multipart/alternative"),

		/** FORM */
		APPLICATION_FORM("application/x-www-form-urlencoded"),
		/** FORM */
		MULTIPART_FORM_DATA("multipart/form-data");

		private final String contentType;

		ContentType(String contentType) {
			this.contentType = contentType;
		}

		public String getContentType() {
			return contentType;
		}
	}
}
