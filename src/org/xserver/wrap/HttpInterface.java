package org.xserver.wrap;

import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;

/**
 * The identify of class is a legal interface. The interface like
 * java.io.Serializable not support any abstract method, because implement
 * HttpInterface just mean you are ready to create wrap the http, not mean the
 * wrapper class should accept http request or have response something. This is
 * prerequisite. Use Http prototype to request and response, class implement
 * HttpInterface should use two parameters, {@link XServerHttpRequest} and
 * {@link XServerHttpResponse}
 * 
 * @author postonzhang
 * 
 */
public interface HttpInterface {
}
