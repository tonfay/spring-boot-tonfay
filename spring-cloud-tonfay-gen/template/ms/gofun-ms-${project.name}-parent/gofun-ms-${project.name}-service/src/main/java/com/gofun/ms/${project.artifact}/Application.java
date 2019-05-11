package com.gofun.ms.${project.artifact};

<#if components?? && components.clean_cache??>
import com.gofun.ms.cachem.EnableCacheMPlus;
</#if>
<#if components?? && components.mq??>
import com.gofun.ms.common.mq.EnableMqProducer;
</#if>
<#if components?? && components.mongo??>
import com.gofun.ms.mongodb.EnableMongoPlus;
</#if>
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;


@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.gofun"})
@SpringBootApplication
@ComponentScan(basePackages = {"com.gofun"})
@ImportResource("classpath:applicationContext-mybatis.xml")
<#if components?? && components.clean_cache??>
@EnableCacheMPlus
</#if>
<#if components?? && components.mq??>
@EnableMqProducer
</#if>
<#if components?? && components.mongo??>
@EnableMongoPlus
</#if>
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}