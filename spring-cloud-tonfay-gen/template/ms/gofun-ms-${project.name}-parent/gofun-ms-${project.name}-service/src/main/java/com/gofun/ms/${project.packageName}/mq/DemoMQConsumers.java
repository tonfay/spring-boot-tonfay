package com.gofun.ms.${project.packageName}.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.gofun.ms.common.mq.DefaultPushConsumer;
import com.gofun.ms.common.mq.MqProperties;

@Component
@ConditionalOnClass(DefaultMQPushConsumer.class)
@EnableConfigurationProperties(MqProperties.class)
public class DemoConsumers {

	private static final Logger logger = LoggerFactory.getLogger(DemoConsumers.class);

	@Autowired
	private MqProperties mqProperties;

	@Autowired

	@Bean(name = "demoConsumer", initMethod = "init", destroyMethod = "destroy")
	public DefaultPushConsumer demoConsumer() {
		DefaultPushConsumer defaultPushConsumer = new DefaultPushConsumer(mqProperties.getNamesrvAddr(),
				mqProperties.getGroupName(), mqProperties.getTopicName(), "demoTag",
				mqProperties.getBatchMaxSize());
		// 设置监听
		defaultPushConsumer.setMessageListenerConcurrently((msgs, context) -> {
			MessageExt msg = msgs.get(0);
			try {
				if (null != msg) {
					logger.info("demoConsumer --- msg = {}", msg.toString());
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.info("demoConsumer 消息调用接口异常,msg{}", msg.toString());
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		});
		return defaultPushConsumer;
	}
}
