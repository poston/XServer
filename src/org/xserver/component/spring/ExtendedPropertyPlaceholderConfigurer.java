package org.xserver.component.spring;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Extend {@link PropertyPlaceholderConfigurer}, that support to load properties
 * for XServer. Do not add Spring Component annotation, because xserver set the
 * bean at applcationContext.xml
 * 
 * @author postonzhang
 * 
 */

public class ExtendedPropertyPlaceholderConfigurer extends
		PropertyPlaceholderConfigurer {
	private Properties props;

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		this.props = props;
	}

	public Object getProperty(String key) {
		return props.get(key);
	}

	public Properties getProperties() {
		return props;
	}
}
