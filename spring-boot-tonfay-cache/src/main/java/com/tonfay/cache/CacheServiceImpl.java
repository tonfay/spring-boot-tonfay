package com.tonfay.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.tonfay.cache.annotation.GoCacheable;
import com.tonfay.cache.config.MsConfigProperties;
import com.tonfay.cache.model.enums.CacheMode;
import com.tonfay.cache.unsafe.ClassIntrospector;
import com.tonfay.redis.service.RedisService;

public class CacheServiceImpl implements CacheService {

	private final static Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);

	private LruTTLCache lruTTLCache = LruTTLCacheManger.getLruCacheInstance();

	@Autowired
	private MsConfigProperties msConfigProperties;

	@Autowired
	private RedisService redisService;

	final ClassIntrospector ci = new ClassIntrospector();

	@Override
	public void set(String key, Object object) {
		if (key == null) {
			return;
		}
		lruTTLCache.put(key, object);
		redisService.set(key, object);
	}

	@Override
	public void set(String key, Object object, long expire) {
		try {
			if (key == null || !msConfigProperties.isCacheEnabled()) {// 如果缓存开关关闭不走缓存
				return;
			}
			if (isObjectSizeOk(object)) {
				lruTTLCache.put(key, object, expire);
			} else {
				logger.warn("msg1=对象size超过内存上限{}kb,不缓存到jvm,,cacheObj={}", msConfigProperties.getCacheMaxObjectSize(),
						JSON.toJSONString(object));
			}
			redisService.set(key, object, expire);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void set(String key, Object object, GoCacheable goCacheable) {
		try {
			if (key == null || !msConfigProperties.isCacheEnabled()) {// 如果缓存开关关闭不走缓存
				return;
			}
			if (goCacheable.cacheMode().equals(CacheMode.BOTH)) {// 只有当CacheMode.BOTH才放jvm内存
				if (goCacheable.forcePutIntoJvm() || isObjectSizeOk(object)) {
					lruTTLCache.put(key, object, goCacheable.expire());
				} else {
					logger.warn("msg1=对象size超过内存上限{}kb,不缓存到jvm,,cacheObj={}",
							msConfigProperties.getCacheMaxObjectSize(), JSON.toJSONString(object));
				}
			}
			redisService.set(key, object, goCacheable.expire());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void set(String key, Object object, long expire, boolean forcePutIntoJvm) {
		try {
			if (key == null || !msConfigProperties.isCacheEnabled()) {// 如果缓存开关关闭不走缓存
				return;
			}
			if (forcePutIntoJvm || isObjectSizeOk(object)) {// 如果超时单条objct内存上限不缓存到jvm，只缓存到redis
				lruTTLCache.put(key, object, expire);
			} else {
				logger.warn("msg1=对象size超过内存上限{}kb,不缓存到jvm,,cacheObj={}", msConfigProperties.getCacheMaxObjectSize(),
						JSON.toJSONString(object));
			}
			redisService.set(key, object, expire);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public Object get(String key) {
		if (!msConfigProperties.isCacheEnabled()) {
			return null;
		}
		Object value = lruTTLCache.get(key);
		if (value != null) {
			logger.info("msg1=aop end,get result from jvmcache,,cacheKey={}", key);
			return value;
		}
		value = redisService.get(key);
		return value;
	}

	@Override
	public Object get(String key, GoCacheable goCacheable) {
		if (!msConfigProperties.isCacheEnabled()) {
			return null;
		}
		Object value = lruTTLCache.get(key);
		if (value != null) {
			logger.info("msg1=aop end,get result from jvmcache,,cacheKey={}", key);
			return value;
		}
		value = redisService.get(key);
		if (value != null && goCacheable != null && goCacheable.cacheMode() == CacheMode.BOTH
				&& (goCacheable.forcePutIntoJvm() || isObjectSizeOk(value))) {
			long ttl = redisService.ttl(key);
			if (ttl > 0) {
				lruTTLCache.put(key, value, ttl);// 时效性设置为redis中ttl剩余时间
			}
		}
		return value;
	}

	private boolean isObjectSizeOk(Object object) {
		if (object == null) {
			return false;
		}
		boolean sizeMactch = true;
		float sizeOfMem = 0;
		try {
			if (object != null) {// 允许object=null的也可以被缓存,如果不为空时才计算对象大小
				sizeOfMem = ci.introspect(object).getDeepSize() / 1024.0f;
				if (sizeOfMem > msConfigProperties.getCacheMaxObjectSize()) {
					sizeMactch = false;
				}
			}
			logger.debug("msg1=对象{},实际大小{}kb", object.getClass().getName(), sizeOfMem);
		} catch (Exception e) {
			sizeMactch = false;
			logger.error(e.getMessage(), e);
		}
		return sizeMactch;
	}

	@Override
	public void remove(String key) {
		redisService.delete(key);
		lruTTLCache.remove(key);
	}
}
