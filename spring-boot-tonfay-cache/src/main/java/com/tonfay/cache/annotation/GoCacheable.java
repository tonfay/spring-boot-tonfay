package com.tonfay.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tonfay.cache.model.enums.CacheMode;

/**
 * 方法级缓存 标注了这个注解的方法返回值将会被缓存
 * 
 */
@Documented
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GoCacheable {

	/**
	 * 缓存过期时间，单位是秒 默认7200秒(2小时)
	 */
	int expire() default 7200;

	/**
	 * 缓存key,支持自定义 如果没有设置使用默认方式 自定义key+请求参数
	 * 
	 * @return
	 */
	String key() default "";

	/**
	 * 是否强制放进jvm缓存不管缓存对象上限 默认不强制 只有当CacheMode.BOTH时forcePutIntoJvm才生效
	 * 
	 * @return
	 */
	boolean forcePutIntoJvm() default false;

	/**
	 * 缓存模式,默认为jvm/redis都放
	 * 
	 * @return
	 */
	CacheMode cacheMode() default CacheMode.BOTH;

}
