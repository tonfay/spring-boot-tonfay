package com.tonfay.cache;

import com.tonfay.cache.annotation.GoCacheable;

/**
 * 缓存
 * 使用本地jvm缓存和redis缓存
 * 取值时,优先顺序 本地缓存取 > redis缓存 > DB
 */
public interface CacheService {

    /**
     * 添加一个缓存，不过期
     * 同时会添加到本地缓存和redis
     */
    public void set(String key, Object object);

    /**
     * 添加一个缓存，指定缓存过期时间
     * 同时会添加到本地缓存和redis
     * 如果单条对象超过上限也不放入jvm
     * @param key 缓存KEY
     * @param object 缓存值
     * @param expire 有效期，单位为秒
     */
    public void set(String key, Object object, long expire);

    /**
     * 添加一个缓存，指定缓存过期时间
     * @param key
     * @param object
     * @param expire
     * @param forcePutIntoJvm 是否强制放进缓存不管缓存对象上限
     */
    public void set(String key, Object object, long expire , boolean forcePutIntoJvm);

    /**
     * 根据Gocacheable设置缓存
     * @param key
     * @param object
     * @param goCacheable
     */
    public void set(String key, Object object, GoCacheable goCacheable);

    /**
     * 获取cache时，如果jvm里不存在则从redis取，
     * 如果redis取到应用goCacheable设置的Both模式,也放入jvm里
     * @param key
     * @param goCacheable
     * @return
     */
    public Object get(String key, GoCacheable goCacheable);

    /**
     * 获取一个缓存值
     */
    public Object get(String key);

    /**
     * 请求redis和jvm缓存
     * @param key
     */
    public void remove(String key);

}
