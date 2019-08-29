package com.cn.mq.revc.config;

import java.util.UUID;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cn.mq.revc.util.RabbitPublicParam;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

@Configuration
public class RabbitMqConfiguration {
	@Bean
	public SimpleMessageListenerContainer simpleMessageListenerContainer(
			CachingConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
		// 注入链接工厂
		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		// 设置监听的队列 可传递多个
		simpleMessageListenerContainer.setQueueNames("spr_topic_queue","spr_fount_queue");
		// 设置消费者数量
		simpleMessageListenerContainer.setConcurrentConsumers(1);
		// 设置最大消费者数量
		simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
		// 消息是否重回队列
		simpleMessageListenerContainer.setDefaultRequeueRejected(true);
		// 类似 channel qos 设置消息数量
		simpleMessageListenerContainer.setPrefetchCount(1);
//		simpleMessageListenerContainer.setMessageConverter(messageConverter)
		// Auto 自动
		// simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
		// 消息自动确认的监听设置监听
		// simpleMessageListenerContainer.setMessageListener(new
		// MessageListener() {
		// @Override
		// public void onMessage(Message message) {
		// System.err.println("客户端获取到的消息为:" + new String(message.getBody()));
		// System.err.println("ConsumerTag" +
		// message.getMessageProperties().getConsumerTag());
		// }
		// });
		// MANUAL 手动
		simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		//监听  这里直接写实现  正式代码 需要编写实体类进行继承接口实现方法
		simpleMessageListenerContainer
				.setMessageListener(new ChannelAwareMessageListener() {
					@Override
					public void onMessage(Message message, Channel channel)
							throws Exception {
						try {
							System.out.println("channel + :" + channel.toString() + "channel.getChannelNumber() + :" + channel.getChannelNumber());
							System.err.println("获取到的消息为："+ new String(message.getBody(), "UTF-8"));
							Thread.sleep(10000);//模拟业务处理
							//消息确认
							channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
							System.err.println("业务处理完成 进行消息确认！");
							System.out.println("channel" + channel.toString() + "channel.getChannelNumber()" + channel.getChannelNumber());
						} catch (Exception e) {
							channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
							e.printStackTrace();
						}
					}
				});
		// 消费者标签无用
//		 simpleMessageListenerContainer.setConsumerTagStrategy(new
//			 ConsumerTagStrategy() {
//			 @Override
//			 public String createConsumerTag(String s) {
//			 return s + "☆" + UUID.randomUUID().toString();
//			 }
//		 });
		return simpleMessageListenerContainer;
	}

	/*----------------------------------------------fount exchange-----------------------------------------------*/
	/* 声明队列 */
	@Bean
	public Queue fountQueue() {
		System.err.println("初始化队列");
		/*
		 * String name, boolean durable, boolean exclusive, boolean autoDelete,
		 * Map<String, Object> arguments
		 */
		return new Queue(RabbitPublicParam.SPRFOUNTQUEUE, false, false, false,
				null);
	}

	/* 声明交换机 */
	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(RabbitPublicParam.SPRFOUNTEXCHANGENAME,
				false, false, null);
	}

	/* 交换机与队列进行绑定 */
	@Bean
	public Binding fanoutExchangeAndQueueBinding() {
		/*
		 * String destination, DestinationType destinationType, String exchange,
		 * String routingKey, Map<String, Object> arguments
		 */
		return new Binding(RabbitPublicParam.SPRFOUNTQUEUE,
				Binding.DestinationType.QUEUE,
				RabbitPublicParam.SPRFOUNTEXCHANGENAME,
				RabbitPublicParam.SPRFOUNTEXCHANGEROUTINGKEY, null);
	}

	/*----------------------------------------------topic exchange-----------------------------------------------*/
	@Bean
	public Queue topictQueue() {
		return new Queue(RabbitPublicParam.SPRTOPICQUEUE, false, false, false,
				null);
	}

	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange(RabbitPublicParam.SPRTOPICEXCHANGENAME, false,
				false, null);
	}

	@Bean
	public Binding topicBinding() {
		return new Binding(RabbitPublicParam.SPRTOPICQUEUE,
				DestinationType.QUEUE, RabbitPublicParam.SPRTOPICEXCHANGENAME,
				RabbitPublicParam.SPRTOPICEXCHANGEROUTINGKEY + ".#", null);
	}

	/*----------------------------------------------Direct exchange-----------------------------------------------*/
	@Bean
	public Queue directQueue() {
		System.err.println("初始化 direct Queue   :"
				+ RabbitPublicParam.SprDirectQueue);
		return new Queue(RabbitPublicParam.SprDirectQueue, false, false, false,
				null);
	}

	@Bean
	public DirectExchange directExchange() {
		System.err.println("初始化 direct exchange    :"
				+ RabbitPublicParam.SprDirectExchangeName);
		return new DirectExchange(RabbitPublicParam.SprDirectExchangeName,
				false, false, null);
	}

	@Bean
	public Binding directBinding() {
		System.err.println("初始化 binding  ququeName : "
				+ RabbitPublicParam.SprDirectQueue + "Exchange Name :"
				+ RabbitPublicParam.SprDirectExchangeName + " routingKey   :"
				+ RabbitPublicParam.SprDirectExchangeRoutingKey);
		return new Binding(RabbitPublicParam.SprDirectQueue,
				DestinationType.QUEUE, RabbitPublicParam.SprDirectExchangeName,
				RabbitPublicParam.SprDirectExchangeRoutingKey, null);
	}

}
