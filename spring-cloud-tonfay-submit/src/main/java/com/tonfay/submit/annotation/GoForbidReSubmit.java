package com.tonfay.submit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tonfay.submit.enums.ForbidReSubmitTypeEnum;

/**
 * 禁止重复提交
 * 为了解决在分布式系统中同次请求被多次调用时数据一致性的问题
 * 使用场景：需要同次请求被多次调用时数据一致
 * 原理：使用拦截器拦截请求，如果方法上有@GoForbidReSubmit
 * 取key, keyFrom, 如果没有设置采用默认ruid(header)中。
 * 如果设置了key,从request中取到相应的key组成一个业务唯一标识
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GoForbidReSubmit {

    /**
     * 关键key
     * key是本次请求中参数的键，
     * 重复请求的key取自header中的ruid
     * 用来标识这个请求的唯一性
     * 拦截器中会使用key从请求参数中获取value
     * @return
     */
    String key() default "";

    /**
     * 自定义key的前缀用来区分业务
     */
    String perFix();
    
    /**
     * 禁止重复提交的模式
     * 默认是全部使用
     */
    ForbidReSubmitTypeEnum forbidReSubmitType() default ForbidReSubmitTypeEnum.ALL;


}
