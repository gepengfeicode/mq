package com.cn.mq.send;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.mq.send.util.RabbitPublicParam;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RabbitMQApp.class)
public class TestRabbiMQ {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void fountSendMsg(){
		//MessagePostProcessor messagePostProcessor, CorrelationData correlationData
//		MessagePostProcessor messagePostProcessor = new 
		try{
			//测试 发送Fount类型的消息
//			rabbitTemplate.convertAndSend(RabbitPublicParam.SPRFOUNTEXCHANGENAME, RabbitPublicParam.SPRFOUNTEXCHANGEROUTINGKEY, "测试消息");
			MessageProperties properties = new MessageProperties();
			//设置发送的消息为持久化
			properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
			//放入请求头
			properties.setHeader("name", "未");
			//放入请求头 
			properties.setHeader("wei", "wang");
			//过期时间
			properties.setExpiration("10000");
			//消息对象
			Message msg = new Message("测试发送Fount类型消息".getBytes(),properties);
			
			CorrelationData correlationData = new CorrelationData("1233211111111111111111");
//			rabbitTemplate.send(RabbitPublicParam.SPRFOUNTEXCHANGENAME, RabbitPublicParam.SPRFOUNTEXCHANGEROUTINGKEY, msg, correlationData);
			rabbitTemplate.convertAndSend(RabbitPublicParam.SPRFOUNTEXCHANGENAME, RabbitPublicParam.SPRFOUNTEXCHANGEROUTINGKEY, "测试延迟队列", new MessagePostProcessor() {
				
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					// TODO Auto-generated method stub
					//重点
					message.getMessageProperties().setHeader("x-delay",5000);
					System.err.println("消息信息为:" + message.getMessageProperties().getHeaders().get("x-delay"));
					return message;
				}
			});
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	@Test
	public void topicSendMsg(){
		try{
			//final String exchange, final String routingKey,final Message message, final CorrelationData correlationData
			//测试 发送Fount类型的消息
			MessageProperties properties = new MessageProperties();
			//设置发送的消息为持久化
			properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
			//放入请求头
			properties.setHeader("name", "topic method");
			//放入请求头 
			properties.setHeader("wei", "wang");
			//过期时间  如果消息未在有效期内被消费则消息被销毁
//			properties.setExpiration("10000");
			//消息对象
			CorrelationData correlationData = new CorrelationData("123321");
			for (int i = 0; i < 5; i++) {
				String message= "测试发送Topic类型消息"+i + "";
				Message msg = new Message(message.getBytes(),properties);
				rabbitTemplate.send(RabbitPublicParam.SPRTOPICEXCHANGENAME,RabbitPublicParam.SPRTOPICEXCHANGEROUTINGKEY+".1",msg,correlationData);
			}
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	@Test
	public void directSendMsg(){
		//测试 发送Fount类型的消息
		MessageProperties properties = new MessageProperties();
		//设置发送的消息为持久化
		properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		//放入请求头
		properties.setHeader("name", "direct method");
		//放入请求头 
		properties.setHeader("wei", "wang");
		//过期时间  如果消息未在有效期内被消费则消息被销毁
		properties.setExpiration("10000");
		//消息对象
		Message msg = new Message("测试发送Fount类型消息".getBytes(),properties);
		CorrelationData correlationData = new CorrelationData("6765334567");
		rabbitTemplate.send(RabbitPublicParam.SprDirectExchangeName, RabbitPublicParam.SprDirectExchangeRoutingKey, msg, correlationData);
		while (true) {}
	}
}
