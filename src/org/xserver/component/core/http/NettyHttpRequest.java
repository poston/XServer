package org.xserver.component.core.http;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.multipart.Attribute;
import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.common.util.CharsetUtil;
import org.xserver.common.util.StringUtil;
import org.xserver.component.exception.IllegalParameterError;

/**
 * TODO add javadoc
 */
public class NettyHttpRequest extends DefaultHttpRequest implements
		HttpRequestCookieAware {

	/** default charset */
	private Charset decodeCharset = CharsetUtil.UTF_8;

	/** cookie decoder */
	private CookieDecoder cookieDecoder;
	/** query string decoder */
	private QueryStringDecoder queryStringDecoder;
	private HttpPostRequestDecoder postDecoder;

	/** client cookies */
	private Cookie[] cookies;
	/** client cookie map */
	private Map<String, String> cookieMap;
	/** get method parameter values */
	private Map<String, List<String>> parameters;
	/** post method parameter values */
	private Map<String, List<String>> parametersByPost;
	private List<FileUpload> fileUploads = new ArrayList<FileUpload>();

	/** client address */
	private SocketAddress remoteAddress;
	/** server address */
	private SocketAddress localAddress;

	/** channel between server and client */
	private Channel channel;

	/** HTTP request type */
	private RequestType requestType = RequestType.HTTP;

	private Map<String, Object> attachments;

	public static final String JSESSIONID = "jsessionid";

	private static final Logger log = LoggerFactory
			.getLogger(NettyHttpRequest.class);

	public NettyHttpRequest(HttpVersion httpVersion, HttpMethod method,
			String uri) {
		super(httpVersion, method, uri);
		if (attachments == null) {
			attachments = new HashMap<String, Object>();
		}
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public RequestType getRequestType() {
		return requestType;
	}

	// *****************************Query String*****************************
	/**
	 * Set and Cache request interface parameters
	 * 
	 * @return parameters Map
	 */
	private Map<String, List<String>> getParameters() {
		if (parameters == null) {
			parameters = getQueryStringDecoder().getParameters();
		}

		return parameters;
	}

	public Map<String, String> getKVParameters() {
		if (parameters == null) {
			parameters = getQueryStringDecoder().getParameters();
		}

		Map<String, String> kvParameters = new HashMap<String, String>();
		for (Entry<String, List<String>> kv : parameters.entrySet()) {
			kvParameters.put(kv.getKey(), kv.getValue().get(0));
		}

		return kvParameters;
	}

	/**
	 * Set and Cache post request interface parameters
	 * 
	 * @return parameters Map
	 */
	private Map<String, List<String>> getParametersByPost() {
		if (parametersByPost == null) {
			parametersByPost = getParameterMap();
		}

		return parametersByPost;
	}

	public HttpPostRequestDecoder getPostDecoder() {
		try {
			if (postDecoder == null) {
				postDecoder = new HttpPostRequestDecoder(this);
			}

			return postDecoder;

		} catch (Exception e) {
			log.error("new HttpPostRequestDecoder instance error.", e);
			return null;
		}

	}

	public void setPostDecoder(HttpPostRequestDecoder postDecoder) {
		this.postDecoder = postDecoder;
	}

	/**
	 * Resolve post request parameter to Map<String, List<String>>
	 * 
	 * @return the post request parameters
	 */
	private Map<String, List<String>> getParameterMap() {
		Map<String, List<String>> parametersByPost = new HashMap<String, List<String>>();

		try {
			if (isPostMethod()) {
				List<InterfaceHttpData> datas = getPostDecoder()
						.getBodyHttpDatas();

				if (datas != null) {
					for (InterfaceHttpData data : datas) {
						if (data instanceof Attribute) {
							Attribute attribute = (Attribute) data;
							String key = attribute.getName();
							String value = attribute.getValue();

							List<String> list = new ArrayList<String>();
							list.add(value);

							parametersByPost.put(key, list);
						} else if (data instanceof FileUpload) {
							FileUpload fileUpload = (FileUpload) data;
							fileUploads.add(fileUpload);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Decoder Post Http Message error.", e);
		}

		return parametersByPost;
	}

	/**
	 * Whether the request method is post, if yes return true, else return false
	 */
	@Override
	public boolean isPostMethod() {
		HttpMethod httpMethod = getMethod();
		if (httpMethod.equals(HttpMethod.POST)
				|| httpMethod.equals(HttpMethod.PUT)) {
			return true;
		}

		return false;
	}

	/**
	 * Get the request Decoder
	 * 
	 * @return the queryStringDecoder
	 */
	private QueryStringDecoder getQueryStringDecoder() {
		if (queryStringDecoder == null) {
			queryStringDecoder = new QueryStringDecoder(getUri(), decodeCharset);
		}

		return queryStringDecoder;
	}

	/**
	 * Get the URI path
	 * 
	 * <pre>
	 * for example:
	 * +-------------------------------------------+
	 * |               URI             |   path    |
	 * +-------------------------------------------+
	 * |http://www.abc.com/x/y?i=1&j=2 |    x/y    |
	 * +-------------------------------------------+
	 * </pre>
	 * 
	 * @return the URI path
	 */
	@Override
	public String getPath() {
		return getQueryStringDecoder().getPath();
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	@Override
	public SocketAddress getLocalAddress() {
		return localAddress;
	}

	public void setRemoteAddress(SocketAddress address) {
		remoteAddress = address;
	}

	public void setLocalAddress(SocketAddress address) {
		localAddress = address;
	}

	/**
	 * Get boolean parameter
	 */
	private Boolean getBoolean(String key, String value) {
		if (value == null || value.equals("")) {
			throw new IllegalArgumentException(
					"The parameter ["
							+ key
							+ "] 's value should not be null, interface should be contain the value.");
		}

		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("y")
				|| value.equals("1")) {
			return true;
		}

		return false;
	}

	/**
	 * Get byte parameter
	 */
	private Byte getByte(String key, String value) {
		if (value == null || value.equals("")) {
			throw new IllegalArgumentException(
					"The parameter ["
							+ key
							+ "] 's value should not be null, interface should be contain the value.");
		}

		try {
			return Byte.valueOf(value);
		} catch (Exception e) {
			throw new IllegalParameterError(key, value,
					IllegalParameterError.Type.Byte);
		}
	}

	/**
	 * Get integer parameter
	 */
	private Integer getInt(String key, String value) {
		if (value == null || value.equals("")) {
			throw new IllegalArgumentException(
					"The parameter ["
							+ key
							+ "] 's value should not be null, interface should be contain the value.");
		}

		try {
			return Integer.valueOf(value);
		} catch (Exception e) {
			throw new IllegalParameterError(key, value,
					IllegalParameterError.Type.Integer);
		}
	}

	/**
	 * Get short parameter
	 */
	private Short getShort(String key, String value) {
		if (value == null || value.equals("")) {
			throw new IllegalArgumentException(
					"The parameter ["
							+ key
							+ "] 's value should not be null, interface should be contain the value.");
		}

		try {
			return Short.valueOf(value);
		} catch (Exception e) {
			throw new IllegalParameterError(key, value,
					IllegalParameterError.Type.Integer);
		}
	}

	/**
	 * Get long parameter
	 */
	private Long getLong(String key, String value) {
		if (value == null || value.equals("")) {
			throw new IllegalArgumentException(
					"The parameter ["
							+ key
							+ "] 's value should not be null, interface should be contain the value.");
		}

		try {
			return Long.valueOf(value);
		} catch (Exception e) {
			throw new IllegalParameterError(key, value,
					IllegalParameterError.Type.Long);
		}
	}

	/**
	 * Get float parameter
	 */
	private Float getFloat(String key, String value) {
		if (value == null || value.equals("")) {
			throw new IllegalArgumentException(
					"The parameter ["
							+ key
							+ "] 's value should not be null, interface should be contain the value.");
		}

		try {
			return Float.valueOf(value);
		} catch (Exception e) {
			throw new IllegalParameterError(key, value,
					IllegalParameterError.Type.Float);
		}
	}

	/**
	 * Get double parameter
	 */
	private Double getDouble(String key, String value) {
		if (value == null || value.equals("")) {
			throw new IllegalArgumentException(
					"The parameter ["
							+ key
							+ "] 's value should not be null, interface should be contain the value.");
		}

		try {
			return Double.valueOf(value);
		} catch (Exception e) {
			throw new IllegalParameterError(key, value,
					IllegalParameterError.Type.Double);
		}
	}

	/**
	 * Get the request parameter value by key
	 * 
	 * @param key
	 *            the request parameter key
	 * @return the request parameter value
	 */
	@Override
	public String getParameter(String key) {
		if (StringUtil.isEmpty(key)) {
			throw new IllegalArgumentException(
					"Argument should not be null or empty.");
		}

		List<String> value = getParameters().get(key);
		if (value == null) {
			return null;
		}

		return value.get(0);
	}

	/**
	 * Get the request parameter value by key, if the value is null return
	 * defaultValue
	 * 
	 * @param key
	 *            the request parameter key
	 * @param defaultValue
	 *            default request parameter value
	 * @return request parameter value if the request parameter value is not
	 *         null, or else defaultValue
	 */
	public String getParameter(String key, String defaultValue) {
		String value = getParameter(key);
		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	/**
	 * Grain the request boolean parameter by key
	 */
	public Boolean getParameterBoolean(String key) {
		return getBoolean(key, getParameter(key));
	}

	/**
	 * Grain the request byte parameter by key
	 */
	public Byte getParameterByte(String key) {
		return getByte(key, getParameter(key));
	}

	/**
	 * Grain the request short parameter by key
	 */
	public Short getParameterShort(String key) {
		return getShort(key, getParameter(key));
	}

	/**
	 * Grain the request integer parameter by key
	 */
	public Integer getParameterInteger(String key) {
		return getInt(key, getParameter(key));
	}

	/**
	 * Grain the request long parameter by key
	 */
	public Long getParameterLong(String key) {
		return getLong(key, getParameter(key));
	}

	/**
	 * Grain the request float parameter by key
	 */
	public Float getParameterFloat(String key) {
		return getFloat(key, getParameter(key));
	}

	/**
	 * Grain the request double parameter by key
	 */
	public Double getParameterDouble(String key) {
		return getDouble(key, getParameter(key));
	}

	/**
	 * Grain the request boolean parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Boolean getParameterBoolean(String key, Boolean defaultValue) {
		try {
			return getParameterBoolean(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request byte parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Byte getParameterByte(String key, Byte defaultValue) {
		try {
			return getParameterByte(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request int parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Short getParameterShort(String key, Short defaultValue) {
		try {
			return getParameterShort(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request int parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Integer getParameterInt(String key, int defaultValue) {
		try {
			return getParameterInteger(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request long parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Long getParameterLong(String key, long defaultValue) {
		try {
			return getParameterLong(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request float parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Float getParameterFloat(String key, float defaultValue) {
		try {
			return getParameterFloat(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request double parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Double getParameterDouble(String key, double defaultValue) {
		try {
			return getParameterDouble(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request string array parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param split
	 *            split string
	 * @return string array by split the value that get by key
	 */
	public String[] getParameterStringArray(String key, String split) {
		String value = getParameter(key);
		if (StringUtil.isEmpty(value)) {
			throw new IllegalArgumentException(
					"The parameter ["
							+ key
							+ "] 's value should not be null, interface should be contain the value.");
		}

		String[] values = value.split(split);
		return values;
	}

	/**
	 * Grain the request string array parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default string array value
	 * @param split
	 * @return if the request has value, just return the string array by split,
	 *         else return defaultValue
	 */
	public String[] getParameterStringArray(String key, String[] defaultValue,
			String split) {
		try {
			return getParameterStringArray(key, split);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request integer array parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param split
	 *            split string
	 * @return integer array by split the value that get by key
	 */
	public int[] getParameterIntegerArray(String key, String split) {
		String[] values = getParameterStringArray(key, split);

		int[] result = new int[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = Integer.parseInt(values[i]);
		}

		return result;
	}

	/**
	 * Grain the request integer array parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param split
	 *            split string
	 * @return integer array by split the value that get by key
	 */
	public Integer[] getParameterIntegerArray0(String key, String split) {
		String[] values = getParameterStringArray(key, split);

		Integer[] result = new Integer[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = Integer.parseInt(values[i]);
		}

		return result;
	}

	/**
	 * Grain the request integer array parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default integer array value
	 * @param split
	 * @return if the request has value, just return the integer array by split,
	 *         else return defaultValue
	 */
	public int[] getParameterIntegerArray(String key, int[] defaultValue,
			String split) {
		try {
			return getParameterIntegerArray(key, split);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request integer array parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default integer array value
	 * @param split
	 * @return if the request has value, just return the integer array by split,
	 *         else return defaultValue
	 */
	public Integer[] getParameterIntegerArray(String key,
			Integer[] defaultValue, String split) {
		try {
			return getParameterIntegerArray0(key, split);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request long array parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param split
	 *            split string
	 * @return long array by split the value that get by key
	 */
	public long[] getParameterLongArray(String key, String split) {
		String[] values = getParameterStringArray(key, split);

		long[] result = new long[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = Long.parseLong(values[i]);
		}

		return result;
	}

	/**
	 * Grain the request long array parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default long array value
	 * @param split
	 * @return if the request has value, just return the long array by split,
	 *         else return defaultValue
	 */
	public long[] getParameterLongArray(String key, long[] defaultValue,
			String split) {
		try {
			return getParameterLongArray(key, split);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the request double array parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param split
	 *            split string
	 * @return double array by split the value that get by key
	 */
	public double[] getParameterDoubleArray(String key, String split) {
		String[] values = getParameterStringArray(key, split);

		double[] result = new double[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = Double.parseDouble(values[i]);
		}

		return result;
	}

	/**
	 * Grain the request integer array parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default integer array value
	 * @param split
	 * @return if the request has value, just return the integer array by split,
	 *         else return defaultValue
	 */
	public double[] getParameterDoubleArray(String key, double[] defaultValue,
			String split) {
		try {
			return getParameterDoubleArray(key, split);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the post request string parameter by key
	 */
	@Override
	public String getParameterByPost(String key) {
		if (StringUtil.isEmpty(key)) {
			throw new IllegalArgumentException(
					"Argument should not be null or empty.");
		}

		List<String> value = getParametersByPost().get(key);

		if (value == null) {
			return null;
		}

		return value.get(0);
	}

	/**
	 * Grain the post request string parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public String getParameterByPost(String key, String defaultValue) {
		String value = getParameterByPost(key);
		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	/**
	 * Grain the post request boolean parameter by key
	 */
	public Boolean getParameterBooleanByPost(String key) {
		return getBoolean(key, getParameterByPost(key));
	}

	/**
	 * Grain the post request integer parameter by key
	 */
	public Integer getParameterIntegerByPost(String key) {
		return getInt(key, getParameterByPost(key));
	}

	/**
	 * Grain the post request float parameter by key
	 */
	public Float getParameterFloatByPost(String key) {
		return getFloat(key, getParameterByPost(key));
	}

	/**
	 * Grain the post request double parameter by key
	 */
	public Double getParameterDoubleByPost(String key) {
		return getDouble(key, getParameterByPost(key));
	}

	/**
	 * Grain the post request long parameter by key
	 */
	public Long getParameterLongByPost(String key) {
		return getLong(key, getParameterByPost(key));
	}

	/**
	 * Grain the post request long parameter by key
	 */
	public Short getParameterShortByPost(String key) {
		return getShort(key, getParameterByPost(key));
	}

	/**
	 * Grain the post request byte parameter by key
	 */
	public Byte getParameterByteByPost(String key) {
		return getByte(key, getParameterByPost(key));
	}

	/**
	 * Grain the post request Integer parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Integer getParameterIntegerByPost(String key, int defaultValue) {
		try {
			return getParameterIntegerByPost(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the post request float parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Float getParameterFloatByPost(String key, float defaultValue) {
		try {
			return getParameterFloatByPost(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the post request double parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Double getParameterDoubleByPost(String key, double defaultValue) {
		try {
			return getParameterDoubleByPost(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the post request long parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Long getParameterLongByPost(String key, long defaultValue) {
		try {
			return getParameterLongByPost(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the post request short parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public short getParameterShortByPost(String key, short defaultValue) {
		try {
			return getParameterShortByPost(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the post request boolean parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Boolean getParameterBooleanByPost(String key, boolean defaultValue) {
		try {
			return getParameterBooleanByPost(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Grain the post request byte parameter by key
	 * 
	 * @param key
	 *            request parameter
	 * @param defaultValue
	 *            default value
	 * @return if the request has value, just return the value, else return
	 *         defaultValue
	 */
	public Byte getParameterByteByPost(String key, Byte defaultValue) {
		try {
			return getParameterByteByPost(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Get client send file list
	 * 
	 * @return client send files
	 */
	public List<FileUpload> getFileUpload() {
		if (parametersByPost == null) {
			parametersByPost = getParameterMap();
		}
		return fileUploads;
	}

	public Map<String, Object> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, Object> attachments) {
		this.attachments = attachments;
	}

	public Object getAttachment(String key) {
		if (StringUtil.isEmpty(key)) {
			return null;
		}
		if (attachments == null) {
			return null;
		} else {
			return attachments.get(key);
		}
	}

	public void setAttachment(String key, Object attachment) {
		if (attachments == null) {
			attachments = new HashMap<String, Object>();
		}
		attachments.put(key, attachment);
	}

	// *****************************Cookie*****************************
	/**
	 * Get the request Decoder, if this instance not null just return, else new
	 * and return
	 * 
	 * @return the cookieDecoder
	 */
	@Override
	public CookieDecoder getCookieDecoder() {
		if (cookieDecoder == null) {
			cookieDecoder = new CookieDecoder();
		}

		return cookieDecoder;
	}

	/**
	 * Get all client cookie, return map
	 * 
	 * @return cookie key-value map
	 */
	@Override
	public Map<String, String> getCookieMap() {
		if (cookieMap == null) {
			Set<Cookie> cookies;
			String value = headers().get(HttpHeaders.Names.COOKIE);
			if (value == null) {
				cookies = Collections.emptySet();
			} else {
				cookies = getCookieDecoder().decode(value);
			}

			if (cookies != null && cookies.size() > 0) {
				cookieMap = new HashMap<String, String>(cookies.size());
				for (Cookie cookie : cookies) {
					cookieMap.put(cookie.getName(), cookie.getValue());
				}
			} else {
				cookieMap = Collections.emptyMap();
			}
		}

		return cookieMap;
	}

	/**
	 * Get the cookie by provided key
	 * 
	 * @param key
	 *            provided key
	 * @return the cookie's value
	 */
	@Override
	public String getCookie(String key) {
		return getCookieMap().get(key);
	}

	/**
	 * Get all client cookies
	 * 
	 * @return cookie array
	 */
	@Override
	public Cookie[] getCookies() {
		if (cookies == null) {
			Set<Cookie> cookies;
			String value = headers().get(HttpHeaders.Names.COOKIE);
			if (value == null) {
				cookies = Collections.emptySet();
			} else {
				cookies = getCookieDecoder().decode(value);
			}

			this.cookies = cookies.toArray(new Cookie[0]);
		}

		return cookies;
	}
}
