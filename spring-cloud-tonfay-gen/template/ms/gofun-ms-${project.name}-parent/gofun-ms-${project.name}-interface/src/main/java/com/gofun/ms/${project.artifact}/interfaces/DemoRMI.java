package com.gofun.ms.${project.artifact}.interfaces;

<#if components?? && components.cache??>
import com.gofun.ms.common.annotation.CacheMode;
import com.gofun.ms.common.annotation.GoCacheable;
</#if>
import com.gofun.ms.common.response.Response;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name= "gofun-ms-${project.name}-service")
public interface DemoRMI {
	@RequestMapping(value = "/testRedis", method = RequestMethod.POST)
	<#if components?? && components.cache??>
	@GoCacheable(expire = 600, cacheMode = CacheMode.BOTH, forcePutIntoJvm = false, key = "testRedis")
	</#if>
	public Response<String> testRedis(String str_key);


	@RequestMapping(value = "/testMongo", method = RequestMethod.POST)
	<#if components?? && components.cache??>
	@GoCacheable(expire = 600, cacheMode = CacheMode.BOTH, forcePutIntoJvm = false, key = "testMongo")
	</#if>
	public Response<String> testMongo(String str_key);

}


