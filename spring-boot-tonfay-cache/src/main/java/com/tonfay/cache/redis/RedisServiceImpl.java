package com.tonfay.cache.redis;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import redis.clients.jedis.JedisCommands;

public class RedisServiceImpl implements RedisService {

	Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);

	private static final Integer DEFAULT_exceed_SECONDS = 30 * 24 * 60 * 60;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 默认30天过期
	 */
	@Override
	public void set(String key, Object object) {
		set(key, object, DEFAULT_exceed_SECONDS);
	}

	@Override
	public void set(String key, Object object, long timeout) {
		if (key == null) {
			return;
		}
		try {
			long starttime = System.currentTimeMillis();
			redisTemplate.opsForValue().set(key, object, timeout, TimeUnit.SECONDS);
			logger.debug("msg1=redis set success,,cacheKey={},,cacheObj={},,cacheExpire={},executeTime={}ms",key, object,timeout,(System.currentTimeMillis()-starttime));
		} catch (Exception e) {
			logger.error("redis set failed,,cacheKey="+key, e);
			return;
		}
	}

	@Override
	public List<Object> getZset(String key, long start, long ent){
		Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, ent);
		List<Object> reverseRange = Lists.newArrayList();
		Iterator<ZSetOperations.TypedTuple<Object>> iterator = tuples.iterator();
		while (iterator.hasNext())
		{
			ZSetOperations.TypedTuple<Object> typedTuple = iterator.next();
			reverseRange.add(typedTuple.getValue() + "," + typedTuple.getScore() );
		}

		return reverseRange;

	}

    @Override
    public void addZset(String key,Object value,double score){
        redisTemplate.opsForZSet().add(key, value, score);
    }

	@Override
	public Object get(String key) {
		if (key == null) {
			return null;
		}
		Object object = null;
		try {
			long starttime = System.currentTimeMillis();
			object = redisTemplate.opsForValue().get(key);
			logger.debug("msg1=redis get success,,cacheKey={},,executeTime={}ms",key,(System.currentTimeMillis()-starttime));
		} catch (Exception e) {
			logger.error("redis get operation failed,,cacheKey={}",key, e);
			return object;
		}

		return object;
	}

	@Override
	public int boundValueOps(String key) {
		if (key == null) {
			return 0;
		}
		String object = null;

		try {
			object = redisTemplate.boundValueOps(key).get(0, -1);
			//TODO stringutil
			object = object == null ? null : object.trim();
			if(object == null || object.length() == 0) {
				return 0;
			}
		} catch (Exception e) {
			logger.error("redis boundValueOps failed,,cacheKey="+key, e);
			return 0;
		}

		return Integer.parseInt(object);
	}

	@Override
	public Object getAndSet(String key, Object value, long timeout) {
		try {
			Object object = redisTemplate.opsForValue().getAndSet(key, value);
			redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
			return object;
		} catch (Exception e) {
			logger.error("redis getAndSet failed,,cacheKey="+key, e);
		}
		return null;
	}

	@Override
	public void delete(String key) {
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			logger.error("redis delete failed,,cacheKey="+key, e);
		}

	}

	/**
	 * bitmap
	 *
	 * @param offset
	 *            例： 手机号
	 * @param value
	 *            true false
	 */
	@Override
	public boolean setBit(String key, long offset, boolean value) {
		return redisTemplate.opsForValue().setBit(key, offset, value);
	}

	/**
	 * bitmap
	 */
	@Override
	public boolean getBit(String key, long offset) {
		return redisTemplate.opsForValue().getBit(key, offset);
	}

	/**
	 *
	 * @param key
	 * @param delta
	 * @return
	 */
	@Override
	public int increment(String key, long delta, long timeout) {
		Long increment = redisTemplate.opsForValue().increment(key, delta);
		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		return increment.intValue();
	}

	/**
	 * 存储redis队列 顺序存储
	 */
	public Long leftPush(String key, String value) {
		return redisTemplate.opsForList().leftPush(key, value);
	}

	public Long rightPush(String key, String value) {
		return redisTemplate.opsForList().rightPush(key, value);
	}

	/**
	 * 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端
	 *
	 * //@param key
	 */
	public String rightPopAndLeftPush(String sourceKey, String destinationKey) {
		return (String) redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
	}

	/**
	 * 移除队列
	 */
	public String rightPop(String key) {
		return (String) redisTemplate.opsForList().rightPop(key);
	}

	/**
	 * 栈/队列长
	 */
	public Long length(String key) {
		return redisTemplate.opsForList().size(key);
	}

	/**
	 * 指定key 失效剩余时间
	 */
	public Long ttl(String key) {
		return redisTemplate.getExpire(key);
	}

	/**
	 * hash 自增
	 */
	public Long increment(String name, String key, Long offset) {
		// 正常的序列化反序列化需要在配置文件里面指定，但是考虑到已经有用户进行使用，故在此配置
		StringRedisSerializer serializer = new StringRedisSerializer();
		redisTemplate.setHashKeySerializer(serializer);
		redisTemplate.setHashValueSerializer(serializer);
		return redisTemplate.boundHashOps(name).increment(key, offset);
	}

	/**
	 * 指定keyName 失效时间
	 */
	public Boolean expire(String keyName, Long time, TimeUnit unit) {
		StringRedisSerializer serializer = new StringRedisSerializer();
		redisTemplate.setHashKeySerializer(serializer);
		redisTemplate.setHashValueSerializer(serializer);
		return redisTemplate.expire(keyName, time, unit);
	}

	public Map<Object, Object> getAllHashVal(String namespace) {
		StringRedisSerializer serializer = new StringRedisSerializer();
		redisTemplate.setHashKeySerializer(serializer);
		redisTemplate.setHashValueSerializer(serializer);
		BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(namespace);
		Set<Object> keys = operations.keys();
		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		for (Object key : keys) {
			hashMap.put(key, operations.get(key));
		}
		return hashMap;
	}

	/**
	 * 保存redis
	 */
	public void addHashSetExpire(String key, String userID, String json, Integer USER_TIME_OUT) {
		redisTemplate.opsForHash().put(key, userID, json);
		redisTemplate.expire(userID, USER_TIME_OUT, TimeUnit.SECONDS);
	}

	/**
	 * 重新设置过期时间
	 *
	 * @author cuiqh
	 */
	public void setHashSetExpire(String key, String userID, Integer USER_TIME_OUT) {
		redisTemplate.expire(userID, USER_TIME_OUT, TimeUnit.SECONDS);
	}

	/**
	 * 获取redis
	 */
	public String getHashObject(String key, String adminId) {
		String str = (String) redisTemplate.opsForHash().get(key, adminId);
		return str;
	}

	/**
	 * 根据token查询user
	 */
	@Override
	public String getAminUserByToken(String key, String token) {
		String str = (String) redisTemplate.opsForHash().get(key, token);
		return str;
	}

	/**
	 * 删除Hash
	 */
	@Override
	public void deleteHash(String userRedisName, String token) {
		redisTemplate.opsForHash().delete(userRedisName, token);
	}

	@Override
	public void saveHashObject(String key, String name, String value) {
		redisTemplate.opsForHash().put(key, name, value);
	}

	/**
	 * 分布式锁
	 *
	 * @param key
	 *            分布式锁key
	 * @param expireTime
	 *            持有锁的最长时间 (redis过期时间) 秒为单位
	 * @return 返回获取锁状态 成功失败
	 * @author lijianye
	 */
	@Override
	public boolean tryLock(String key, int expireTime) {
		final JSONObject lock = new JSONObject();
		lock.put("id",key);
		lock.put("st", new Date().getTime());// startTime
		lock.put("ks", expireTime);// keepSeconds
		return this.tryLock(key, "", expireTime);
	}

	@Override
	public boolean tryLock(String key, final String value, int expireTime) {
		String result = redisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				connection.select(0);
				JedisCommands commands = (JedisCommands) connection.getNativeConnection();
				return commands.set(key, value, "NX", "EX", expireTime);
			}
		});
		return !StringUtils.isEmpty(result);
	}

	/**
	 * 释放分布式锁
	 *
	 * @param key
	 *            分布式锁key
	 * @author lijianye
	 */
	@Override
	public void unLock(String key) {
		redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				connection.select(0);
				JedisCommands commands = (JedisCommands) connection.getNativeConnection();
				return commands.del(key);
			}
		});
	}

	@Override
	public void updateLock(String key, String value, int expireTime) {
		redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				connection.select(0);
				JedisCommands commands = (JedisCommands) connection.getNativeConnection();
				commands.set(key, value);
				return commands.expire(key, expireTime);
			}
		});
	}

	@Override
	public String getLockInfo(String key) {
		return redisTemplate.execute(new RedisCallback<String>() {
			@Override
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				connection.select(0);
				JedisCommands commands = (JedisCommands) connection.getNativeConnection();
				return commands.get(key);
			}
		});
	}

	/**
	 * 生成自增序列
	 */
	@Override
	public long generate(String key, Date expireTime) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
		counter.expireAt(expireTime);
		return counter.incrementAndGet();
	}

	@Override
	public long generate(String key) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
		return counter.incrementAndGet();
	}

	@Override
	public Object getHash(String key, String hashKey) {
		if (key == null) {
			return null;
		}
		Object object = null;

		try {
			object = redisTemplate.opsForHash().get(key, hashKey);
		} catch (Exception e) {
			logger.error(String.format("redis get operation for value failed by key [%s]: %s",key, e.getMessage()));
			return object;
		}

		return object;
	}

	@Override
	public void setHash(String key, String hashKey, Object object, long timeout) {
		if (key == null) {
			return;
		}
		try {
			redisTemplate.opsForHash().put(key, hashKey, object);
			redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.error("redis set operation for value failed,,cacheKey="+key+",,hashkey="+hashKey, e);
			return;
		}
	}

	/**
	 * 遍历set集合中所有元素
	 */
	@Override
	public Set<Object> getSetKeys(String key) {
		StringRedisSerializer serializer = new StringRedisSerializer();
		redisTemplate.setHashKeySerializer(serializer);
		redisTemplate.setHashValueSerializer(serializer);
		return redisTemplate.opsForSet().members(key);
	}

	/**
	 * 添加一个set集合，指定缓存过期时间
	 *
	 * @param key
	 *            缓存KEY
	 * @param object
	 *            缓存值
	 */
	@Override
	public void setSet(String key, Object object, long timeout) {
		setSet(key, object, timeout, null);
	}

	@Override
	public void setSet(String key, Object object, long timeout, TimeUnit timeUnit) {
		if (key == null) {
			return;
		}
		try {
			redisTemplate.opsForSet().add(key, object);
			redisTemplate.expire(key, timeout, timeUnit == null ? TimeUnit.SECONDS : timeUnit);
		} catch (Exception e) {
			logger.error("redis set set operation for value failed,cacheKey={}",key, e);
			return;
		}
	}

	@Override
	public Long getSetSize(String key) {
		if (key == null) {
			return null;
		}
		return redisTemplate.opsForSet().size(key);
	}

	@Override
	public Object getStringtValueSerializer(String key) {
		redisTemplate.setDefaultSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.afterPropertiesSet();
		Object object = redisTemplate.opsForValue().get(key);
		redisTemplate.setDefaultSerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		return object;
	}

	@Override
	public void setStringtValueSerializer(String key, Object value,long timeout) {
		redisTemplate.setDefaultSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.afterPropertiesSet();
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.setDefaultSerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());
	}

	@Override
	public void deleteSerializer(String key) {
		redisTemplate.setDefaultSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.afterPropertiesSet();
		redisTemplate.delete(key);
		redisTemplate.setDefaultSerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new StringRedisSerializer());
	}

	@Override
	public Boolean hasKey(String key) {
		redisTemplate.setDefaultSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
		redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.afterPropertiesSet();
		return redisTemplate.hasKey(key);
	}
}
