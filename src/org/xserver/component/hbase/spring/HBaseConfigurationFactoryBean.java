package org.xserver.component.hbase.spring;

import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.xserver.component.hbase.util.HBaseUtil;

/**
 * The FactoryBean will create {@link org.apache.hadoop.conf.Configuration}
 * bean. We always put hbase-site.xml in the classpath, so the
 * <code>HBaseConfigurationFactoryBean</code>'s some properties could be miss,
 * if some hbase characteristic should be set, use hbase.properties file to
 * configuration
 * 
 * @author postonzhang
 * 
 */
public class HBaseConfigurationFactoryBean implements InitializingBean,
		DisposableBean, FactoryBean<Configuration> {

	private boolean deleteConnection = true;
	private boolean stopProxy = true;
	private Configuration configuration;
	private Configuration hadoopConfig;
	private Properties hbaseProperties;
	private String quorum;
	private Integer port;

	public void setDeleteConnection(boolean deleteConnection) {
		this.deleteConnection = deleteConnection;
	}

	public void setStopProxy(boolean stopProxy) {
		this.stopProxy = stopProxy;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public Configuration getObject() throws Exception {
		return configuration;
	}

	@Override
	public Class<? extends Configuration> getObjectType() {
		return (configuration != null ? configuration.getClass()
				: Configuration.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public Properties getHbaseProperties() {
		return hbaseProperties;
	}

	public void setHbaseProperties(Properties hbaseProperties) {
		this.hbaseProperties = hbaseProperties;
	}

	public void setZKQuorum(String quorum) {
		this.quorum = quorum;
	}

	public void setZKPort(Integer port) {
		this.port = port;
	}

	@Override
	public void destroy() throws Exception {
		if (deleteConnection) {
			HConnectionManager.deleteConnection(getObject(), stopProxy);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		configuration = (hadoopConfig != null ? HBaseConfiguration
				.create(hadoopConfig) : HBaseConfiguration.create());

		HBaseUtil.addProperties(configuration, hbaseProperties);

		if (StringUtils.hasText(quorum)) {
			configuration.set(HConstants.ZOOKEEPER_QUORUM, quorum.trim());
		}
		if (port != null) {
			configuration
					.set(HConstants.ZOOKEEPER_CLIENT_PORT, port.toString());
		}
	}
}
