package com.tonfay.cache.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="tonfay",ignoreUnknownFields = true)
public class MsConfigProperties {

    /**
     * 默认开启缓存
     */
    private boolean cacheEnabled = true;

    /**
     *  缓存队列上限
     */
    private int cacheMaxQueueSize = 30000 ;

    /**
     *  缓存单条对象最大上限大小，单位kb
     */
    private int cacheMaxObjectSize = 20;

    /**
     * 是否开启缓存
     * @return
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public int getCacheMaxQueueSize() {
        return cacheMaxQueueSize;
    }

    public void setCacheMaxQueueSize(int cacheMaxQueueSize) {
        this.cacheMaxQueueSize = cacheMaxQueueSize;
    }

    public int getCacheMaxObjectSize() {
        return cacheMaxObjectSize;
    }

    public void setCacheMaxObjectSize(int cacheMaxObjectSize) {
        this.cacheMaxObjectSize = cacheMaxObjectSize;
    }
}