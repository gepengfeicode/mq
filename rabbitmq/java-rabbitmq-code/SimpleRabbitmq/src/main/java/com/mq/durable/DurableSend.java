package com.mq.durable;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.impl.AMQBasicProperties;

/**
 * 持久化 包括
 * 		队列持久化
 * 		创建队列   参数解析  1.队列名称  2.是否持久化   3.是否独占  4.是否字段删除 5.属性参数
 * 		channel.queueDeclare(PublicParam.DURABLE_QUEU_NAME, true, false, false, null);
 * 		消息持久化
 *		 发送消息 deliveryMode 取值 2 消息持久化  1 不持久化  
 * 		AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().deliveryMode(2).build();
		channel.basicPublish(PublicParam.DURABLE_DIRECT_EXCHANGE, PublicParam.DURABLE_DIRECT_ROUTINGKEY, basicProperties, "测试持久化 消息  队列 交换机".getBytes());
 * 		交换机持久化
 * 		创建交换机 参数解析 1.交换机名称 2.交换机类型 3.是否持久化
 * 		channel.exchangeDeclare(PublicParam.DURABLE_DIRECT_EXCHANGE, BuiltinExchangeType.DIRECT, true);
 * @author Administrator
 *
 */
public class DurableSend {
	public static void main(String[] args) throws Exception, Exception {
		/*创建链接*/
		Connection connection = ConnectionUtils.connection();
		/*创建管道*/
		Channel channel =  connection.createChannel();
		/*创建队列   参数解析  1.队列名称  2.是否持久化   3.是否独占  4.是否字段删除 5.属性参数*/
		channel.queueDeclare(PublicParam.DURABLE_QUEU_NAME, true, false, false, null);
		/*创建交换机 参数解析 1.交换机名称 2.交换机类型 3.是否持久化*/
		channel.exchangeDeclare(PublicParam.DURABLE_DIRECT_EXCHANGE, BuiltinExchangeType.DIRECT, true);
		/*队列与交换机进行绑定*/
		channel.queueBind(PublicParam.DURABLE_QUEU_NAME, PublicParam.DURABLE_DIRECT_EXCHANGE, PublicParam.DURABLE_DIRECT_ROUTINGKEY);
		/*发送消息 deliveryMode 取值 2 消息持久化  1 不持久化  */
		AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().deliveryMode(2).build();
		channel.basicPublish(PublicParam.DURABLE_DIRECT_EXCHANGE, PublicParam.DURABLE_DIRECT_ROUTINGKEY, basicProperties, "测试持久化 消息  队列 交换机".getBytes());
		
		System.gc();
		System.exit(1);

	
	}
}
