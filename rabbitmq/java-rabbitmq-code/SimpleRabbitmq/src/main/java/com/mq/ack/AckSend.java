package com.mq.ack;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 消息确认消息发送者    
 * 		消息确认机制
 * @author Administrator
 */
public class AckSend {
	public static void main(String[] args) throws Exception {
		/*链接*/
		Connection connection = ConnectionUtils.connection();
		/*管道*/
		Channel channel = connection.createChannel();
		/*声明队列*/
		channel.queueDeclare(PublicParam.ACK_QUEUE_NAME,false, false, true, null);
		/*声明交换机*/
		channel.exchangeDeclare(PublicParam.ACK_TOPIC_EXCHANGE,BuiltinExchangeType.TOPIC , false, true, null);
//		/*交换机与队列绑定*/
		channel.queueBind( PublicParam.ACK_QUEUE_NAME,PublicParam.ACK_TOPIC_EXCHANGE, PublicParam.ACK_ROUTING_KEY+".#", null);
//		/*发布消息*/
		channel.basicPublish(PublicParam.ACK_TOPIC_EXCHANGE,  PublicParam.ACK_ROUTING_KEY+".1", null, "测试 Msg ack".getBytes());
		channel.basicPublish(PublicParam.ACK_TOPIC_EXCHANGE,  PublicParam.ACK_ROUTING_KEY+".1", null, "测试 Msg ack1".getBytes());
		System.err.println("消息发送成功  Success" );
		System.exit(1);
	}
}
