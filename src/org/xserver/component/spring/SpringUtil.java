package org.xserver.component.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring Integrated into XServer aim at reducing the relationship between BO,
 * PO and DAO, the member <code>APPLICATIONCONTEXT</code> is the system objects
 * context, so if want to use Spring Dependence Injection must use getBean not
 * use constructor to new object.
 * 
 * @author postonzhang
 * @since 2013/2/25
 * 
 */
public class SpringUtil {

	public static final Logger logger = LoggerFactory
			.getLogger(SpringUtil.class);

	public static final String APPLICATIONCONTEXTPATH = "applicationContext.xml";

	public static ApplicationContext APPLICATIONCONTEXT;

	public static ApplicationContext loadApplicationContext() {
		if (APPLICATIONCONTEXT == null) {
			logger.info("load application for spring.");
			APPLICATIONCONTEXT = new ClassPathXmlApplicationContext(
					APPLICATIONCONTEXTPATH);
		}

		return APPLICATIONCONTEXT;
	}

	public static ApplicationContext getApplicationContext() {
		assert APPLICATIONCONTEXT != null;
		return APPLICATIONCONTEXT;
	}

	public static Object getBean(String className) {
		assert APPLICATIONCONTEXT != null;
		return APPLICATIONCONTEXT.getBean(className);
	}

	public static Object getBean(Class<?> clazz) {
		assert APPLICATIONCONTEXT != null;
		return APPLICATIONCONTEXT.getBean(clazz);
	}
}
