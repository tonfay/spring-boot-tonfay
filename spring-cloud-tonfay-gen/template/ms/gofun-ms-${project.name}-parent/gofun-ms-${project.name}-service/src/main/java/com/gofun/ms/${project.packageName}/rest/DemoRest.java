package com.gofun.ms.${project.packageName}.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
<#if components?? && components.redis??>
import com.gofun.ms.common.annotation.GoForbidReSubmit;
import com.gofun.ms.common.redis.RedisService;
import com.gofun.ms.common.response.Response;
</#if>
<#if components?? && components.mongo??>
import org.springframework.data.mongodb.core.MongoTemplate;
</#if>

@RestController
@RequestMapping("/demo")
public class DemoRest {
	<#if components?? && components.redis??>
	@Autowired
	private RedisService redisService;
	</#if>
	<#if components?? && components.mongo??>
	@Autowired
	private MongoTemplate mongoTemplate;
	</#if>

	<#if components?? && components.redis??>
	@GoForbidReSubmit(key = "str_key", perFix = "MS_DEMO_REDIS_")//防重提交
	@RequestMapping(value = "/testRedis", method = RequestMethod.GET)
	@ResponseBody
	public Response<String> testRedis(String str_key) {
		redisService.get("str_key");
		return new Response<>();
	}
	</#if>

	<#if components?? && components.mongo??>
	@GoForbidReSubmit(key = "str_key", perFix = "MS_DEMO_Mongo_")//防重提交
	@RequestMapping(value = "/testMongo", method = RequestMethod.GET)
	@ResponseBody
	public Response<String> testMongo(String str_key) {
		mongoTemplate.getCollectionNames();
		return new Response<>();
	}
	</#if>
}
