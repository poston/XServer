package org.xserver.component.elasticsearch.spring;

import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

import java.util.Properties;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.xserver.common.util.StringUtil;

/**
 * The factoryBean is responsible for creating TransportClient instance, the
 * clusterNodes should be configured before using. The initialize
 * TransportClient can configure <strong>clusterName</strong> and
 * <strong>clientTransportSniff</strong>, or provide
 * <strong>elasticsearch.properties</strong> to support configure
 * 
 * <h3>Config</h3>
 * 
 * <ul>
 * <strong>TransportClient</strong>
 * <li>clusterName : the elasticsearch cluster name, that be equal to the
 * cluster.name property in the elasticsearch.yml file.</li>
 * <li>clusterNodes : the whole node in elasticsearch cluster, the property
 * should match <strong>'host:port,{n}'</strong> pattern. The port can miss, if
 * so, the port is default <strong>9300</strong></li>
 * <li>clientTransportSniff : The client allows to sniff the rest of the
 * cluster, and add those into its list of machines to use. In this case, note
 * that the ip addresses used will be the ones that the other nodes were started
 * with (the "publish" address). In order to enable it, set the
 * client.transport.sniff to true</li>
 * </ul>
 * 
 * @author postonzhang
 * @since 2013/09/09
 * 
 */
public class TransportClientFactoryBean implements InitializingBean,
		DisposableBean, FactoryBean<TransportClient> {

	private static final Logger log = LoggerFactory
			.getLogger(TransportClientFactoryBean.class);

	private TransportClient client;
	private String clusterNodes;
	private String clusterName;
	private Properties properties;
	private Boolean clientTransportSniff;
	private static final String COMMA = ",";
	private static final String COLON = ":";
	private static final int DEFAULT_PORT = 9300;

	@Override
	public TransportClient getObject() throws Exception {
		return this.client;
	}

	@Override
	public Class<TransportClient> getObjectType() {
		return TransportClient.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public void destroy() throws Exception {
		log.info("closing transportclient");
		try {
			if (client != null) {
				client.close();
			}
		} catch (Exception e) {
			log.error("closing transportclient error", e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("instance TransportClient");
		client = new TransportClient(settings());

		Assert.hasText(clusterNodes,
				"clusterNodes must be configured, check at elasticsearchContext.xml");
		for (String clusterNode : split(clusterNodes, COMMA)) {
			String hostname = substringBefore(clusterNode, COLON);
			String port = substringAfter(clusterNode, COLON);

			Assert.hasText(hostname,
					"hostname must be configured, check at elasticsearchContext.xml");

			int _port = DEFAULT_PORT;
			if (!StringUtil.isEmpty(port)) {
				_port = Integer.parseInt(port);
			}

			client.addTransportAddress(new InetSocketTransportAddress(hostname,
					_port));
			log.debug("add transport address {}:{}", hostname, _port);
		}

		client.connectedNodes();
	}

	private Settings settings() {
		if (properties != null) {
			return settingsBuilder().put(properties).build();
		}

		return settingsBuilder().put("cluster.name", clusterName)
				.put("client.transport.sniff", clientTransportSniff).build();
	}

	public void setClient(TransportClient client) {
		this.client = client;
	}

	public void setClusterNodes(String clusterNodes) {
		this.clusterNodes = clusterNodes;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setClientTransportSniff(Boolean clientTransportSniff) {
		this.clientTransportSniff = clientTransportSniff;
	}

}
