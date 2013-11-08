package org.xserver.component.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DynamicLoadBean implements ApplicationContextAware {

	private ConfigurableApplicationContext configurableApplicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
	}

	public ConfigurableApplicationContext getConfigurableApplicationContext() {
		return configurableApplicationContext;
	}

	public void loadBean(String configLocation) {
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(
				(BeanDefinitionRegistry) getConfigurableApplicationContext()
						.getBeanFactory());
		beanDefinitionReader
				.setResourceLoader(getConfigurableApplicationContext());
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(
				getConfigurableApplicationContext()));

		try {
			String[] configLocations = new String[] { configLocation };

			for (String location : configLocations) {
				beanDefinitionReader
						.loadBeanDefinitions(getConfigurableApplicationContext()
								.getResource(location));
			}
		} catch (BeansException e) {
		}
	}
}
