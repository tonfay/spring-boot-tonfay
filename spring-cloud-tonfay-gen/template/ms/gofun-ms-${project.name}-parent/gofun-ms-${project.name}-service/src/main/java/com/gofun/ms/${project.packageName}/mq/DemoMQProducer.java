package com.gofun.ms.${project.packageName}.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.gofun.ms.common.mq.RocketMQProducer;

public class DemoMQProducer {

	private static final Logger logger = LoggerFactory.getLogger(DemoMQProducer.class);
	@Autowired
	private RocketMQProducer rocketMQProducer;

	public boolean send(String topic ,String tag, Object msg) {
		try {
			SendResult sendResult = rocketMQProducer.send(topic, tag, msg);
			logger.info("订单发送MQ完成: topic={}, tag={}, msg={}, sendResult={}", topic, tag,
					JSONObject.toJSONString(msg), JSONObject.toJSONString(sendResult));
		} catch (Exception e) {
			logger.error("订单发送MQ异常: topic={}, tag={}, msg={}, exception={}", topic, tag,
					JSONObject.toJSONString(msg), e);
			return false;
		}
		return true;
	}

}