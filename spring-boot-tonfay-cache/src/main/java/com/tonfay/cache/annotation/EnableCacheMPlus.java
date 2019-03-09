package com.tonfay.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用清除缓存功能
 * 当使用此注解时,开启清除缓存功能
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
//@Import()
//TODO 待实现,可考虑基于MQ的广播模式实现
public @interface EnableCacheMPlus {
    
}
