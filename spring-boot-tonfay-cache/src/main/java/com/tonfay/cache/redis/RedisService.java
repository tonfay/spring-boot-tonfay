package com.tonfay.cache.redis;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存接口
 */
public interface RedisService {

    /**
     * 添加一个缓存，过期时间为1个月
     */
    public void set(String key, Object object);

    /**
     * 添加一个缓存，指定缓存过期时间
     *
     * @param key        缓存KEY
     * @param object     缓存值
     * @param activeTime 有效时常，单位为秒
     */
    public void set(String key, Object object, long activeTime);

    /**
     * @param key
     * @param value
     * @param score
     */
    public void addZset(String key, Object value, double score);

    /**
     * @param key
     * @param start
     * @param ent
     * @return
     */
    public List<Object> getZset(String key, long start, long ent);

    /**
     * 获取一个缓存值
     * 方法内部在redis发生异常时吃掉了异常
     */
    public Object get(String key);

    /**
     * 删除缓存
     */
    public void delete(String key);

    /**
     * 返回给定 key 的旧值。 当 key 没有旧值时，即 key 不存在时，返回 nil 。当 key 存在但不是字符串类型时，返回一个错误。
     *
     * @param timeout 秒
     */
    Object getAndSet(String key, Object value, long timeout);

    /**
     * bitmap
     *
     * @param offset 例： 手机号
     */
    boolean getBit(String key, long offset);

    /**
     * bitmap
     *
     * @param offset 例： 手机号
     * @param value  true false
     */
    boolean setBit(String key, long offset, boolean value);

    /**
     * 原子增加
     */
    public int increment(String key, long delta, long timeout);

    public int boundValueOps(String key);

    public Long leftPush(String key, String value);

    public String rightPopAndLeftPush(String sourceKey, String destinationKey);

    public String rightPop(String key);

    public Long length(String key);

    public Long rightPush(String key, String value);

    public Long ttl(String key);

    public Long increment(String name, String key, Long offset);

    public Boolean expire(String keyName, Long time, TimeUnit unit);

    public Map<Object, Object> getAllHashVal(String namespace);

    public void addHashSetExpire(String loginRedisName, String userID, String json, Integer USER_TIME_OUT);

    public String getHashObject(String loginRedisName, String adminId);

    public String getAminUserByToken(String token, String token2);

    public void deleteHash(String userRedisName, String token);

    public void setHashSetExpire(String key, String userID, Integer USER_TIME_OUT);

    public void saveHashObject(String key, String name, String value);

    /**
     * 分布式锁 释放锁
     *
     * @param key 锁的key
     */
    void unLock(String key);

    void updateLock(String key, String value, int expireTime);

    String getLockInfo(String key);//获取锁信息

    boolean tryLock(String key, String value, int expire);

    boolean tryLock(String key, int expire);

    /**
     * 生成自增序列
     */
    public long generate(String key, Date expireTime);

    /**
     * 生成自增序列
     */
    public long generate(String key);

    public void setHash(String key, String hashKey, Object object, long timeout);

    Object getHash(String key, String hashKey);

    /**
     * 遍历key中所有元素
     */
    public Set<Object> getSetKeys(String key);

    /**
     * 添加一个set集合，指定缓存过期时间
     *
     * @param key        缓存KEY
     * @param object     缓存值
     * @param activeTime 有效时常，单位为秒
     */
    public void setSet(String key, Object object, long activeTime);

    /**
     * 添加一个set集合，指定缓存过期时间
     *
     * @param key        缓存KEY
     * @param object     缓存值
     * @param activeTime 有效时常
     * @param timeUnit   有效时常单位
     */
    public void setSet(String key, Object object, long activeTime, TimeUnit timeUnit);

    /**
     * 取得set集合数量
     *
     * @param key 缓存KEY
     */
    public Long getSetSize(String key);

    public Object getStringtValueSerializer(String key);

    void deleteSerializer(String key);

    Boolean hasKey(String key);

    void setStringtValueSerializer(String key, Object value, long timeout);
}
