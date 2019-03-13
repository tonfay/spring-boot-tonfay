package com.tonfay.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {
	@Bean
	@ConditionalOnMissingBean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {  
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();  
        template.setConnectionFactory(connectionFactory);  
        setMySerializer(template);  
        template.afterPropertiesSet();  
        return template;  
    }
	/** 
     * 设置序列化方法
     */  
    private void setMySerializer(RedisTemplate template) {  
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);// new Jackson2JsonRedisSerializer<Object>(Object.class);  
        ObjectMapper om = new ObjectMapper();  
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);  
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);  
        jackson2JsonRedisSerializer.setObjectMapper(om);  
        template.setKeySerializer(template.getStringSerializer());  
        template.setValueSerializer(jackson2JsonRedisSerializer);  
    }
}
