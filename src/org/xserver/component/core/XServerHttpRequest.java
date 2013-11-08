package org.xserver.component.core;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
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
 * Wrap HttpRequest, Cookie, provider <code>getParamterXXX</code> and
 * <code>getParameterXXXByPost</code> method to gain request parameter(s)
 * 
 * @author postonzhang
 * @since 2013/01/10
 * 
 */
public class XServerHttpRequest extends DefaultHttpRequest {

	private QueryStringDecoder queryStringDecoder;
	private Charset decodeCharset = CharsetUtil.UTF_8;
	private Map<String, List<String>> parameters;
	private Map<String, List<String>> parametersByPost;
	private List<FileUpload> fileUploads = new ArrayList<FileUpload>();
	private SocketAddress remoteAddress;
	private SocketAddress localAddress;

	private static final Logger log = LoggerFactory
			.getLogger(XServerHttpRequest.class);

	public XServerHttpRequest(HttpVersion httpVersion, HttpMethod method,
			String uri) {
		super(httpVersion, method, uri);
	}

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

	/**
	 * <pre>
	 * XServer use Netty default HttpPostRequestDecoder,
	 * default MINSIZE as 16 KB, default charset as UTF-8
	 * </pre>
	 */
	private HttpPostRequestDecoder getHttpPostRequestDecoder() {
		try {
			return new HttpPostRequestDecoder(this);
		} catch (Exception e) {
			log.error("new HttpPostRequestDecoder instance error.", e);
			return null;
		}
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
				List<InterfaceHttpData> datas = getHttpPostRequestDecoder()
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
	private boolean isPostMethod() {
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
	public String getPath() {
		return getQueryStringDecoder().getPath();
	}

	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public SocketAddress getLocalAddress() {
		return localAddress;
	}

	public void setRemoteAddress(SocketAddress address) {
		remoteAddress = address;
	}

	public void setLocalAddress(SocketAddress address) {
		localAddress = address;
	}

	private Boolean getBoolean(String key, String value) {
		if (value == null) {
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

	private Byte getByte(String key, String value) {
		if (value == null) {
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

	private Integer getInt(String key, String value) {
		if (value == null) {
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

	private Long getLong(String key, String value) {
		if (value == null) {
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

	private Float getFloat(String key, String value) {
		if (value == null) {
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

	private Double getDouble(String key, String value) {
		if (value == null) {
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
	public Float getParameterFload(String key, float defaultValue) {
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

	public String[] getParameterStringArray(String key, String[] defaultValue,
			String split) {
		try {
			return getParameterStringArray(key, split);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public int[] getParameterIntegerArray(String key, String split) {
		String[] values = getParameterStringArray(key, split);

		int[] result = new int[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = Integer.parseInt(values[i]);
		}

		return result;
	}

	public Integer[] getParameterIntegerArray0(String key, String split) {
		String[] values = getParameterStringArray(key, split);

		Integer[] result = new Integer[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = Integer.parseInt(values[i]);
		}

		return result;
	}

	public int[] getParameterIntegerArray(String key, int[] defaultValue,
			String split) {
		try {
			return getParameterIntegerArray(key, split);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public Integer[] getParameterIntegerArray(String key,
			Integer[] defaultValue, String split) {
		try {
			return getParameterIntegerArray0(key, split);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public long[] getParameterLongArray(String key, String split) {
		String[] values = getParameterStringArray(key, split);

		long[] result = new long[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = Long.parseLong(values[i]);
		}

		return result;
	}

	public long[] getParameterLongArray(String key, long[] defaultValue,
			String split) {
		try {
			return getParameterLongArray(key, split);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public double[] getParameterDoubleArray(String key, String split) {
		String[] values = getParameterStringArray(key, split);

		double[] result = new double[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = Double.parseDouble(values[i]);
		}

		return result;
	}

	public double[] getParameterDoubleArray(String key, double[] defaultValue,
			String split) {
		try {
			return getParameterDoubleArray(key, split);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static void main(String[] args) {
		XServerHttpRequest request = new XServerHttpRequest(
				HttpVersion.HTTP_1_1, HttpMethod.GET,
				"http://localhost:8080/imc?groupId=");
		int[] param = request.getParameterIntegerArray("groupId", ",");
		System.out.println(param[0]);
	}

	/**
	 * Grain the post request string parameter by key
	 */
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

	public List<FileUpload> getFileUpload() {
		if (parametersByPost == null) {
			parametersByPost = getParameterMap();
		}
		return fileUploads;
	}
}
