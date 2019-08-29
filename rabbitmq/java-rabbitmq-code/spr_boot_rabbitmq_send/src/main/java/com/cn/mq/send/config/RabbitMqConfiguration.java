package com.cn.mq.send.config;

import java.io.UnsupportedEncodingException;
import java.util.Formatter.BigDecimalLayoutForm;
import java.util.Map;

import javax.security.auth.callback.ConfirmationCallback;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cn.mq.send.util.RabbitPublicParam;

/**
 * Rabbitmq配置类
 * 
 * @author Administrator
 * 
 */
@Configuration
public class RabbitMqConfiguration {
	@Autowired/*这里不做自定义的声明因为在application.properties文件中已经声明了对应的属性并且RabbitAutoConfiguration已经进行了CachingConnectionFactory对象的声明*/
	private CachingConnectionFactory cachingConnectionFactory;

	// @Bean
	// public RabbitAdmin rabbitAdmin(){
	// RabbitAdmin rabbitAdmin = new RabbitAdmin(cachingConnectionFactory);
	// rabbitAdmin.setAutoStartup(true);
	// System.err.println("初始化Rabbit Admin");
	// return rabbitAdmin;
	// }
	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(
				cachingConnectionFactory);
		System.err.println("初始化RabbitMQTemplate");
		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
			@Override
			public void returnedMessage(Message message, int replyCode,
					String replyText, String exchange, String routingKey) {
				// TODO Auto-generated method stub
				System.err
						.println("======================未成功路由的消息信息为=================");
				System.err.println("message Info" + message.toString());
				try {
					System.err.println("msg "
							+ new String(message.getBody(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.err.println("replyCode Info" + replyCode);
				System.err.println("replyText Info" + replyText);
				System.err.println("exchange Info" + exchange);
				System.err.println("routingKey Info" + routingKey);
			}
		});
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
			@Override
			public void confirm(CorrelationData correlationData, boolean ack,
					String cause) {
				System.err
						.println("======================消息确认=================");
				System.err.println("correlationData Info" + correlationData);
				System.err.println("ack Info" + ack);
				System.err.println("cause Info" + cause);
			}
		});
		return rabbitTemplate;
	}
	/*----------------------------------------------fount exchange-----------------------------------------------*/
	/* 声明队列 */
	@Bean
	public Queue fountQueue(){
		/*String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments*/
		 return new Queue(RabbitPublicParam.SPRFOUNTQUEUE,false,false,false,null);
	}
	/*声明交换机*/
	@Bean
	public FanoutExchange fanoutExchange(){
		return new FanoutExchange(RabbitPublicParam.SPRFOUNTEXCHANGENAME, false, false, null);
	}
	/*交换机与队列进行绑定*/
	@Bean
	public Binding fanoutExchangeAndQueueBinding(){
		/*String destination, DestinationType destinationType, String exchange, String routingKey,
			Map<String, Object> arguments*/
		return new Binding(RabbitPublicParam.SPRFOUNTQUEUE,Binding.DestinationType.QUEUE,RabbitPublicParam.SPRFOUNTEXCHANGENAME,RabbitPublicParam.SPRFOUNTEXCHANGEROUTINGKEY,null);
	}
	
	/*----------------------------------------------topic exchange-----------------------------------------------*/
	@Bean
	public Queue topictQueue(){
		return new Queue(RabbitPublicParam.SPRTOPICQUEUE, false, false, false,null);
	}
	@Bean
	public TopicExchange topicExchange(){
		return new TopicExchange(RabbitPublicParam.SPRTOPICEXCHANGENAME, false, false, null);
	}
	@Bean
	public Binding topicBinding(){
		return new Binding(RabbitPublicParam.SPRTOPICQUEUE,DestinationType.QUEUE,RabbitPublicParam.SPRTOPICEXCHANGENAME,RabbitPublicParam.SPRTOPICEXCHANGEROUTINGKEY + ".#",null);
	}
	
	/*----------------------------------------------Direct exchange-----------------------------------------------*/
	@Bean
	public Queue directQueue(){
		System.err.println("初始化 direct Queue   :" +RabbitPublicParam.SprDirectQueue) ;
		return new Queue(RabbitPublicParam.SprDirectQueue, false, false, false,null);
	}
	@Bean
	public DirectExchange directExchange(){
		System.err.println("初始化 direct exchange    :" + RabbitPublicParam.SprDirectExchangeName);
		return new DirectExchange(RabbitPublicParam.SprDirectExchangeName, false, false, null);
	}
	@Bean
	public Binding directBinding(){
		System.err.println("初始化 binding  ququeName : " + RabbitPublicParam.SprDirectQueue + "Exchange Name :" + RabbitPublicParam.SprDirectExchangeName +  " routingKey   :" + RabbitPublicParam.SprDirectExchangeRoutingKey);
		return new Binding(RabbitPublicParam.SprDirectQueue,DestinationType.QUEUE,RabbitPublicParam.SprDirectExchangeName,RabbitPublicParam.SprDirectExchangeRoutingKey,null);
	}
}
