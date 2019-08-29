package com.mq.exchange.direct;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 使用Direct类型交换机进行匹配
 * 
 * @author Administrator
 * 
 */
public class DirectExchangeSend {
	public static void main(String[] args) throws Exception, Exception {
		Connection connection = ConnectionUtils.connection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(PublicParam.DIRECT_QUEUE_NAME, false,false, false, null);
		channel.exchangeDeclare(PublicParam.DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false);
		channel.queueBind(PublicParam.DIRECT_QUEUE_NAME,PublicParam.DIRECT_EXCHANGE_NAME, PublicParam.DIRECT_ROUTING_KEY);
		channel.basicPublish(PublicParam.DIRECT_EXCHANGE_NAME,PublicParam.DIRECT_ROUTING_KEY, null, "发送DirectMsg".getBytes());
		System.err.println("消息发送成功！");
	}
}
