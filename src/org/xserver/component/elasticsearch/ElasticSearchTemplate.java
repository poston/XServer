package org.xserver.component.elasticsearch;

import org.elasticsearch.client.Client;

public class ElasticSearchTemplate implements ElasticSearchOperations {
	private Client client;

	public ElasticSearchTemplate(Client client) {
		this.client = client;
	}

	@Override
	public <T> void index(Class<T> clazz) {
		
	}

	@Override
	public <T> T queryForObject(String id, Class<T> clazz) {
		return null;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
