package org.xserver.component.extension.shiro.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.component.extension.shiro.cache.RedisCache;
import org.xserver.component.redis.RedisTemplate;

/**
 * Use redis to cache some thing. 
 * @author postonzhang
 * @since 2015/05/30
 * 
 * @param <K>
 * @param <V>
 */
public class RedisCache<K, V> implements Cache<K, V> {

	private static final Logger log = LoggerFactory.getLogger(RedisCache.class);

	private static final String DEFAULT_REDIS_CACHE_PREFIX = "redis_cache:";

	private static final String WILDCARD = "*";

	private String redisCachePrefix;

	protected RedisTemplate redisTemplate;

	public RedisCache() {
		this(null, DEFAULT_REDIS_CACHE_PREFIX);
	}

	public RedisCache(RedisTemplate redisTemplate) {
		this(redisTemplate, DEFAULT_REDIS_CACHE_PREFIX);
	}

	public RedisCache(RedisTemplate redisTemplate, String redisCachePrefix) {
		this.redisTemplate = redisTemplate;
		this.redisCachePrefix = redisCachePrefix;
	}

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Getting object from cache [redis] for key [{}]", key);
		}
		if (key == null) {
			return null;
		} else {
			return (V) redisTemplate.getValue((String) key);
		}
	}

	@Override
	public V put(K key, V value) throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Putting object in cache [redis] for key [{}]", key);
		}

		try {
			@SuppressWarnings("unchecked")
			V previous = (V) redisTemplate.getValue((String) key);
			redisTemplate.setKeyValue((String) key, (String) value);
			return previous;
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	@Override
	public V remove(K key) throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Clearing all objects from cache [redis]");
		}

		@SuppressWarnings("unchecked")
		V value = (V) redisTemplate.getValue((String) key);
		if (value != null) {
			redisTemplate.delete((String) key);
		}

		return value;
	}

	@Override
	public void clear() throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Clearing all objects from cache [redis]");
		}

		redisTemplate.flushDB();
	}

	@Override
	public int size() {
		try {
			return redisTemplate.dbSize().intValue();
		} catch (Throwable t) {
			new CacheException(t);
		}

		return 0;
	}

	@Override
	public Set<K> keys() {
		try {
			@SuppressWarnings("unchecked")
			Set<K> keys = (Set<K>) redisTemplate.keys(redisCachePrefix
					+ WILDCARD);
			if (keys == null || keys.isEmpty()) {
				return Collections.emptySet();
			}

			return Collections.unmodifiableSet(new LinkedHashSet<K>(keys));
		} catch (Throwable t) {
			new CacheException(t);
		}

		return null;
	}

	@Override
	public Collection<V> values() {
		try {
			Set<K> keys = keys();

			List<V> values = Collections.emptyList();

			if (keys != null && !keys.isEmpty()) {
				values = new ArrayList<V>(keys.size());
				for (K key : keys) {
					values.add(get(key));
				}

				return Collections.unmodifiableList(values);
			}

			return values;
		} catch (Throwable t) {
			new CacheException(t);
		}

		return null;
	}

}
