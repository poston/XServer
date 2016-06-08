package org.xserver.component.extension.shiro.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xserver.component.extension.shiro.cache.RedisCache;
import org.xserver.component.extension.shiro.cache.RedisCacheManager;
import org.xserver.component.redis.RedisTemplate;

/**
 * Use Redis to manager cache.
 * 
 * <h3>Note</h3>
 * 
 * The property of {@link #caches} use map to store redisCache, not for storing
 * user {@link AuthenticationInfo}, user {@link AuthorizationInfo} and others.
 * In other words, all cache use Redis indeed.
 * <p/>
 * 
 * @author postonzhang
 * @since 2015/04/24
 * 
 */
public class RedisCacheManager<K, V> implements CacheManager, Initializable,
		Destroyable {
	private final static Logger log = LoggerFactory
			.getLogger(RedisCacheManager.class);

	protected RedisTemplate redisTemplate;

	private final ConcurrentMap<String, Cache<K, V>> caches = new ConcurrentHashMap<String, Cache<K, V>>();

	@Override
	public void destroy() throws Exception {
	}

	@Override
	public void init() throws ShiroException {
	}

	/**
	 * Get the cache instance not the cache value
	 * 
	 * @return cache instance
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Cache<K, V> getCache(String name) throws CacheException {
		if (log.isTraceEnabled()) {
			log.trace("Acquiring RedisCache instance named [" + name + "]");
		}

		Cache<K, V> cache = caches.get(name);

		if (cache == null) {
			cache = new RedisCache<K, V>(redisTemplate);
			caches.put(name, cache);
		}

		return cache;
	}

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
}
