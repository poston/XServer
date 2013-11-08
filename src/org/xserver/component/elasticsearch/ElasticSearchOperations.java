package org.xserver.component.elasticsearch;

import org.xserver.component.jdbc.JdbcOperations;

/**
 * Like the {@link JdbcOperations}, ElasticSearchOperations just define the
 * basic operations on elasticsearch. The {@link ElasticSearchTemplate} will
 * implements the operations.
 * 
 * @author postonzhang
 * @since 2013/09/09
 */
public interface ElasticSearchOperations {
	/**
	 * Create an index for a class
	 */
	<T> void index(Class<T> clazz);

	/**
	 * Execute the query against elasticsearch and return the first returned
	 * object
	 * 
	 * @param id
	 *            the query condition
	 * @param clazz
	 *            the result class
	 * @return the first matching object
	 */
	<T> T queryForObject(String id, Class<T> clazz);
}
