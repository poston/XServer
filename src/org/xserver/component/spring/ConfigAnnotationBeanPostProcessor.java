package org.xserver.component.spring;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.xserver.common.util.StringUtil;
import org.xserver.component.annotation.Config;
import org.xserver.component.log.StdOutLog;

/**
 * Autowire field's value with {@link Config} annotation, the setting value in
 * properties files. When the properties files have set class fields, the
 * original value will ignore.
 * 
 * @author postonzhang
 * 
 */
@Component
public class ConfigAnnotationBeanPostProcessor extends
		InstantiationAwareBeanPostProcessorAdapter {

	@Resource
	private ExtendedPropertyPlaceholderConfigurer extendedPropertyPlaceholderConfigurer;

	private SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter();

	private static final Logger logger = LoggerFactory
			.getLogger(ConfigAnnotationBeanPostProcessor.class);
	private static final Logger stdOutLog = StdOutLog.getLogger();

	@Override
	public boolean postProcessAfterInstantiation(final Object bean,
			String beanName) throws BeansException {
		ReflectionUtils.doWithFields(bean.getClass(),
				new ReflectionUtils.FieldCallback() {

					@Override
					public void doWith(Field field)
							throws IllegalArgumentException,
							IllegalAccessException {
						if (field.isAnnotationPresent(Config.class)) {
							if (Modifier.isStatic(field.getModifiers())) {
								throw new IllegalStateException(
										"Config annotation should not use on static Field.");
							}

							Config config = field.getAnnotation(Config.class);
							String key = StringUtil.isEmpty(config.value()) ? field
									.getName() : config.value();

							Object value = extendedPropertyPlaceholderConfigurer
									.getProperty(key);

							String split = config.split();
							if (!split.equals("")) {
								assert field.getType().isArray();
								if (split.equals("|")) {
									split = "\\|";
								}
								value = ((String) value).split(split);
							}

							if (value != null) {
								Object _value = simpleTypeConverter
										.convertIfNecessary(value,
												field.getType());
								ReflectionUtils.makeAccessible(field);
								field.set(bean, _value);
								stdOutLog
										.info("config to \"{}.{}\" field with value \"{}\"",
												new Object[] {
														bean.getClass()
																.getName(),
														field.getName(), _value });
								logger.info(
										"config to \"{}.{}\" field with value \"{}\"",
										new Object[] {
												bean.getClass().getName(),
												field.getName(), _value });
							}
						}
					}
				});

		return true;
	}
}
