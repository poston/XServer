package org.xserver.component.extension.filter;

import org.xserver.component.core.XServerHttpContextAttachment;
import org.xserver.component.exception.FilterProcessError;

/**
 * XServer support user-defined some behaves at specific lifecycle.
 * 
 * @author postonzhang
 * @since 2014/3/4
 */
public interface ContextFilter {

	/**
	 * get the filter name
	 * @throws FilterProcessError
	 */
	public abstract String getName();

	/**
	 * when the server start, every filter will be called this method as the
	 * process of initialize context
	 * 
	 * @throws FilterProcessError
	 */
	public abstract void contextInitialized() throws FilterProcessError;

	/**
	 * when the server stop, every filter will be called this method as the
	 * process of resource release or destroy context
	 * 
	 * @throws FilterProcessError
	 */
	public abstract void contextDestroyed() throws FilterProcessError;

	/**
	 * when the request come, if match filter condition(like match path, etc),
	 * the filter will invoke this method as processing context
	 * 
	 * @param attachment
	 * @throws FilterProcessError
	 */
	public abstract void contextProcess(XServerHttpContextAttachment attachment) throws FilterProcessError;

	/**
	 * Get the filter type.
	 * 
	 * @return
	 * @see {@link FilterType}
	 */
	public abstract FilterType getFilterType();

	/**
	 * If return true, ignore {@link #contextProcess(XServerHttpContextAttachment)} throw exception and finally go next filter
	 */
	public abstract boolean isExceptionGoNext();

}
