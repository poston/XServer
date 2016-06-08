package org.xserver.component.extension.filter.parameter;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.xserver.component.core.interfaces.InterfaceContext;
import org.xserver.component.core.interfaces.InterfaceMeta;
import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.exception.FilterProcessError;
import org.xserver.component.extension.filter.AbstractContextFilter;
import org.xserver.component.extension.filter.parameter.util.FilterUtil;

/**
 * The filter is wrapping parameters before into business logic process, filter
 * support {@code GET} and {@code POST} HTTP methods parameters reflect into a
 * javaBean(javaBean's field).
 * 
 * <h3>Event timeline</h3>
 * 
 * <ul>
 * <li>2015/08/10 support basic data type, inner type and one level bean type.</li>
 * <li>2015/10/20 replace one and more level bean type by json style request parameters
 * to javaBean(use jackson lib to reflect to javaBean).</li>
 * </ul>
 * 
 * @author postonzhang
 * @since 2015/08/10
 * 
 */
public class XServerParametersProcessFilter extends AbstractContextFilter {
	private static final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

	/**
	 * reflect request parameters to interface's arguments
	 */
	@Override
	public void process(XServerHttpContextAttachment attachment) throws FilterProcessError {
		try {
			InterfaceContext context = getInterfaceContext();

			XServerHttpRequest request = attachment.getRequest();
			InterfaceMeta meta = context.getInterfaceMeta(request.getPath());

			Method method = meta.getMethod();

			Class<?>[] classes = method.getParameterTypes();
			String[] keys = discoverer.getParameterNames(method);

			List<Object> parameterValues = FilterUtil.getParameterValues(attachment, keys, classes);

			attachment.attachment(XServerParametersProcessFilter.class.getSimpleName(), parameterValues);
		} catch (Exception e) {
			throw new FilterProcessError(XServerParametersProcessFilter.class.getSimpleName(), e.getMessage());
		}
	}
}
