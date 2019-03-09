package com.tonfay.cache;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tonfay.cache.config.MsConfigProperties;

/**
 * 定时清理过期缓存
 */
public class LruTTLCacheManger {
	private static final Logger logger = LoggerFactory.getLogger(LruTTLCacheManger.class);
	private static int MAX_CACHE_SIZE = 20000;// 默认最大2W条数据
	@Autowired
	private MsConfigProperties msConfigProperties;
	private static LruTTLCache lruCacheInstance = null;
	private static final ScheduledExecutorService scheduledExecutorService = Executors
			.newSingleThreadScheduledExecutor();

	public LruTTLCacheManger() {
		MAX_CACHE_SIZE = msConfigProperties.getCacheMaxQueueSize();
	}

	public static LruTTLCache getLruCacheInstance() {
		if (lruCacheInstance == null) {
			synchronized (LruTTLCacheManger.class) {
				if (lruCacheInstance == null) {
					lruCacheInstance = new LruTTLCache(MAX_CACHE_SIZE);
					startClearExpiredTask();
				}
			}
		}
		return lruCacheInstance;
	}

	public static void shutdown() {
		scheduledExecutorService.shutdown();
	}

	/**
	 * 启动清理过期任务
	 */
	public static void startClearExpiredTask() {
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("msg1=清理过期缓存开始");
					lruCacheInstance.clearExpried();
					logger.info("msg1=清理过期缓存结束");
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}
			}
		}, 2, 2, TimeUnit.MINUTES);
	}

	public static void main(String[] args) {
		for (int i = 1000000; i > 0; i--) {
			LruTTLCacheManger.getLruCacheInstance().put("a" + i, "a" + i);
		}
		long begin = System.currentTimeMillis();
		List<String> res = LruTTLCacheManger.getLruCacheInstance().searchKeys("a");
		long end = System.currentTimeMillis();
		long size = LruTTLCacheManger.getLruCacheInstance().size();
		System.out.println("fff" + size);
		System.out.println(end - begin);
		System.out.println("1");
	}

}
