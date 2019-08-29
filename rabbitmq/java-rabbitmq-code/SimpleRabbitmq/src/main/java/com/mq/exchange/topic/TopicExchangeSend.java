package com.mq.exchange.topic;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
/**
 * Exchange Type  is Topic
 * 
 * like mate
 * 模糊匹配
 * @author Administrator
 *
 */
public class TopicExchangeSend {
	public static void main(String[] args) throws IOException, Exception {
		Connection connection = ConnectionUtils.connection();
		//创建链接
		Channel channel = connection.createChannel();
		//创建交换机
		channel.exchangeDeclare(PublicParam.TOPIC_EXCHANGE_NAME,BuiltinExchangeType.TOPIC,false,false,null);
		//创建队列
		channel.queueDeclare(PublicParam.TOPIC_QUEUE_NAME1, false, false, false, null);
		channel.queueDeclare(PublicParam.TOPIC_QUEUE_NAME2, false, false, false, null);
		//队列与Topic exchange进行绑定
		channel.queueBind(PublicParam.TOPIC_QUEUE_NAME1, PublicParam.TOPIC_EXCHANGE_NAME, PublicParam.TOPIC_ROUTING_KEY+".#", null);
		channel.queueBind(PublicParam.TOPIC_QUEUE_NAME2, PublicParam.TOPIC_EXCHANGE_NAME, PublicParam.TOPIC_ROUTING_KEY+"", null);
		//发送消息
		channel.basicPublish(PublicParam.TOPIC_EXCHANGE_NAME, PublicParam.TOPIC_ROUTING_KEY+".first", false, false, null,"测试Topic交换机发送消息".getBytes());
		channel.basicPublish(PublicParam.TOPIC_EXCHANGE_NAME,"-/88", false, false, null,"测试Topic交换机发送消息".getBytes());
		System.err.print("消息发送成功");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
