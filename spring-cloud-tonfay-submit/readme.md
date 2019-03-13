禁止重复提交
幂等性注解
为了解决在分布式系统中同次请求被多次调用时数据一致性的问题

使用场景：需要同次请求被多次调用时数据一致

原理：使用aop拦截请求，如果方法上有@GoForbidReSubmit
取key, keyFrom
如果没有设置采用默认RUID(header)中。
如果设置了key,从request中取到相应的key组成一个业务唯一标识

使用方式:
方法级别引入注解
@GoForbidReSubmit