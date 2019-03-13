package com.tonfay.submit.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gofun.ms.common.annotation.GoForbidReSubmit;
import com.gofun.ms.common.exception.RestException;
import com.gofun.ms.common.redis.RedisService;
import com.gofun.ms.common.response.ResponseCode;
import javax.servlet.http.HttpServletRequest;

import com.gofun.ms.common.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * redislock 注解执行器 处理重复请求 和串行指定条件的请求
 * 两种模式的拦截
 * 1.ruid 是针对每一次请求的
 * 2.key+val 是针对相同参数请求
 * @author: yangming
 * @date : 2018-05-22 12:37
 */
@Order(10)
// 小的先执行
@Aspect
@Component
public class RedisLockAnnoAspect extends BaseAspect {

	public Logger logger = LoggerFactory.getLogger(this.getClass());
	ThreadLocal<String> perFix_key = new ThreadLocal<String>();
	private boolean enable = true;// 配置注解后 默认开启
	@Autowired
	RedisService redisService;

	@Pointcut("@annotation(com.gofun.ms.common.annotation.GoForbidReSubmit)")
	public void watchRedisLockAnno() {
	}

	@Before("watchRedisLockAnno()")
	public void doBefore(JoinPoint joinPoint) {
		GoForbidReSubmit goForbidReSubmit = getAnnotation(joinPoint,GoForbidReSubmit.class);
		if (enable && null != goForbidReSubmit) {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = attributes.getRequest();
			String ruid = request.getHeader("ruid");
			if (StringUtils.isNotBlank(ruid)) {
				Boolean result = redisService.tryLock("ruid:" + ruid, "0", 5 * 60);// 值为0请求未处理 1请求已处理
				if (!result) {
					throw new RestException(ResponseCode.CANNOT_REQUE_AGAIN);
				}
				logger.info("msg1=当前请求(redis14Lock)已成功记录,且标记为0未处理,,ruid={}", ruid);
			} else {
				logger.warn("msg1=header没有设置ruid," + request.getRemoteHost());
				//throw new RestException(ResponseCode.PARAMS_REQUESTID_LOST);
			}
			// 自定义key
			String key = goForbidReSubmit.key();
			String perFix = goForbidReSubmit.perFix();
			if (StringUtils.isNotBlank(key)) {
				String val = "";
				Object[] paramValues = joinPoint.getArgs();
				String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
				for (int i = 0; i < paramNames.length; i++) {
					String params = JSON.toJSONString(paramValues[i]);
					if (params.startsWith("{")) {
						JSONObject jsonObject = JSON.parseObject(params);
						val = jsonObject.getString(key);
					} else if (key.equals(paramNames[i])) {
						val = params;
					}
				}
				if (StringUtils.isNotBlank(val)) {
					perFix = perFix + ":" + val;
					try{
						Boolean result = redisService.tryLock(perFix, 30);
						if (!result) {
							String targetName = joinPoint.getTarget().getClass().getName();
							String methodName = joinPoint.getSignature().getName();
							logger.error("msg1=不允许重复执行,,key={},,targetName={},,methodName={}",perFix,targetName,methodName);
							throw new RestException(ResponseCode.NOT_ALLOW_PARALLEL);
						}
						perFix_key.set(perFix);
						logger.info("msg1=当前请求(redis14Lock)已成功锁定:{}", perFix);
					}catch (RestException e){
						throw e;
					}catch (Exception e){
						LogUtil.error(logger,"获取redis锁发生异常",e);
					}
				}
			}

		}
	}

	@After("watchRedisLockAnno()")
	public void doAfter(JoinPoint joinPoint) throws Throwable {
		try {
			GoForbidReSubmit goForbidReSubmit = getAnnotation(joinPoint,GoForbidReSubmit.class);
			if (enable && null != goForbidReSubmit) {
				ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
				HttpServletRequest request = attributes.getRequest();
				String ruid = request.getHeader("ruid");
				if (StringUtils.isNotBlank(ruid)) {
					String result = redisService.getLockInfo("ruid:" + ruid);// 值为0请求未处理 1请求已处理
					if (StringUtils.isNotBlank(result)) {
						redisService.updateLock("ruid:" + ruid, "1", 5 * 60);
					}
					logger.info("msg1=当前请求(redis14Lock)已成功处理,且标记为1已处理,,ruid={}", ruid);
				}
				// 自定义key
				String key = goForbidReSubmit.key();
				if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(perFix_key.get())) {
					try{
						redisService.unLock(perFix_key.get());
						logger.info("msg1=当前请求(redis14Lock)已成功释放,,key={}", perFix_key.get());
						perFix_key.set(null);
					}catch (Exception e){
						LogUtil.error(logger,"释放redis锁生成异常",e);
					}
				}
			}
		} catch (Exception e) {
			LogUtil.error(logger,e.getMessage(),e);
		}
	}

}