package com.tonfay.cache.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.tonfay.cache.CacheService;
import com.tonfay.cache.annotation.GoCacheable;
import com.tonfay.cache.config.MsConfigProperties;
import com.tonfay.submit.aspect.BaseAspect;

/***
 * 对CacheService所有方法进行AOP
 */
@Order(999)
@Aspect
@Component
public class GoCacheAspect extends BaseAspect {
	
	private final static Logger logger = LoggerFactory.getLogger(GoCacheAspect.class);
	private static final String POINT_CUT = "execution(public * com.tonfay.cache.CacheService.*(..))";
	@Autowired
	private CacheService cacheService;
	@Autowired
	private MsConfigProperties msConfigProperties;
	


	@Pointcut(POINT_CUT)
	public void methodPiontCut() {

	}

	/**
	 * 
	 */
	@Around("methodPiontCut()")
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable{
		String targetName = joinPoint.getTarget().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		Object result = null;
		GoCacheable goCacheable = getAnnotation(joinPoint,GoCacheable.class);
		if(goCacheable != null && msConfigProperties.isCacheEnabled()) {//开启缓存
			int expire = goCacheable.expire();
			String key = goCacheable.key();
			key = getCacheKey(key,targetName, methodName, arguments);
			logger.debug("msg1=aop start,,cacheKey:{}" , key);
			result = cacheService.get(key,goCacheable);
			if(result != null) {//如果缓存命中直接返回缓存中的结果
				return result;
			}else {
				result = joinPoint.proceed();
				if(result != null) {
					cacheService.set(key, result,goCacheable);
					logger.info("msg1=aop end,get result from DB,,cacheKey={},,cacheObj={},,cacheExpire={}",key,result,expire);
				}
			}	
		}else {
			result = joinPoint.proceed();
			logger.warn("msg1=aop end,get result from DB,,gocache.enabled=false");
			return result;
		}
		return result;
	}

}
