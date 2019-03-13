package com.tonfay.submit.aspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tonfay.redis.service.RedisService;
import com.tonfay.submit.annotation.GoForbidReSubmit;
import com.tonfay.submit.enums.ForbidReSubmitTypeEnum;
import com.tonfay.submit.exception.RedisLockException;
import com.tonfay.submit.util.StringUtils;

/**
 * redislock 注解执行器 处理重复请求 和串行指定条件的请求
 * 两种模式的拦截
 * 1.ruid 是针对每一次请求的
 * 2.key+val 是针对相同参数请求
 */
@Aspect
@Component
@ConditionalOnClass(RedisService.class)
public class RedisLockAnnoAspect extends BaseAspect {

	public Logger logger = LoggerFactory.getLogger(this.getClass());
	ThreadLocal<String> perFix_key = new ThreadLocal<String>();
	private boolean enable = true;// 配置注解后 默认开启
	@Autowired
	RedisService redisService;

	@Pointcut("@annotation(com.tonfay.submit.annotation.GoForbidReSubmit)")
	public void watchRedisLockAnno() {
	}
	
	/**
	 * request请求头中的key
	 */
	private static String HEADER_RUID_KEY = "RUID";
	/**
	 * redis中锁的key前缀
	 */
	private static String REDIS_KEY_PERFIX = "RUID:";
	
	/**
	 * 锁等待时长
	 */
	private static int LOCK_WAIT_TIME = 5 * 60 ;
	
	@Before("watchRedisLockAnno()")
	public void doBefore(JoinPoint joinPoint) {
		GoForbidReSubmit goForbidReSubmit = getAnnotation(joinPoint,GoForbidReSubmit.class);
		//判断是否使用注解
		if (enable && null != goForbidReSubmit) {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = attributes.getRequest();
			//1.判断模式
			
			if(goForbidReSubmit.forbidReSubmitType() == ForbidReSubmitTypeEnum.ALL 
					|| goForbidReSubmit.forbidReSubmitType() == ForbidReSubmitTypeEnum.RUID) {
				//2.1.通过ruid模式判断是否属于重复提交
				String ruid = request.getHeader(HEADER_RUID_KEY);
				if (StringUtils.isNotBlank(ruid)) {
					Boolean result = redisService.tryLock(REDIS_KEY_PERFIX + ruid, LOCK_WAIT_TIME);
					if (!result) {
						throw new RedisLockException("命中RUID重复请求");
					}
					logger.debug("msg1=当前请求已成功记录,且标记为0未处理,,{}={}",HEADER_RUID_KEY, ruid);
				} else {
					logger.warn("msg1=header没有ruid,防重复提交功能失效,,remoteHost={}" + request.getRemoteHost());
				}
					
			}
			
			if(goForbidReSubmit.forbidReSubmitType() == ForbidReSubmitTypeEnum.ALL 
					|| goForbidReSubmit.forbidReSubmitType() == ForbidReSubmitTypeEnum.KEY) {
				//2.2.通过自定义key模式判断是否属于重复提交
				String key = goForbidReSubmit.key();
				if (StringUtils.isNotBlank(key)) {
					String val = "";
					Object[] paramValues = joinPoint.getArgs();
					String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
					//获取自定义key的value
					for (int i = 0; i < paramNames.length; i++) {
						String params = JSON.toJSONString(paramValues[i]);
						if (params.startsWith("{")) {
							//如果是对象
							//通过key获取value
							JSONObject jsonObject = JSON.parseObject(params);
							val = jsonObject.getString(key);
						} else if (key.equals(paramNames[i])) {
							//如果是单个k=v
							val = params;
						} else {
							//如果自定义的key,在请求参数中没有此参数,说明非法请求
							logger.warn("自定义的key,在请求参数中没有此参数,防重复提交功能失效");
						}
					}
					
					//判断重复提交的条件
					String perFix = goForbidReSubmit.perFix();
					if (StringUtils.isNotBlank(val)) {
						perFix = perFix + ":" + val;
						try{
							Boolean result = redisService.tryLock(perFix, LOCK_WAIT_TIME);
							if (!result) {
								String targetName = joinPoint.getTarget().getClass().getName();
								String methodName = joinPoint.getSignature().getName();
								logger.error("msg1=不允许重复执行,,key={},,targetName={},,methodName={}",perFix,targetName,methodName);
								throw new RedisLockException("不允许重复提交");
							}
							//存储在当前线程
							perFix_key.set(perFix);
							logger.info("msg1=当前请求已成功锁定:{}", perFix);
						} catch (Exception e){
							logger.error("获取redis锁发生异常",e);
							throw e;
						}
					} else {
						logger.warn("自定义的key,在请求参数中value为空,防重复提交功能失效");
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
				
				if(goForbidReSubmit.forbidReSubmitType() == ForbidReSubmitTypeEnum.ALL 
						|| goForbidReSubmit.forbidReSubmitType() == ForbidReSubmitTypeEnum.RUID) {
					ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
					HttpServletRequest request = attributes.getRequest();
					String ruid = request.getHeader(HEADER_RUID_KEY);
					if (StringUtils.isNotBlank(ruid)) {
						try{
							redisService.unLock(REDIS_KEY_PERFIX + ruid);
							logger.info("msg1=当前请求已成功处理,,ruid={}", ruid);
						}catch (Exception e){
							logger.error("释放redis锁异常",e);
						}
					}
				}
				
				if(goForbidReSubmit.forbidReSubmitType() == ForbidReSubmitTypeEnum.ALL 
						|| goForbidReSubmit.forbidReSubmitType() == ForbidReSubmitTypeEnum.KEY) {
					// 自定义key
					String key = goForbidReSubmit.key();
					if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(perFix_key.get())) {
						try{
							redisService.unLock(perFix_key.get());
							logger.info("msg1=当前请求已成功释放,,key={}", perFix_key.get());
							perFix_key.set(null);
						}catch (Exception e){
							logger.error("释放redis锁异常",e);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

}