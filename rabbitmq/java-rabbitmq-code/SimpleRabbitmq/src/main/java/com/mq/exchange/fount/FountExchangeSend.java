package com.mq.exchange.fount;

import java.io.IOException;
import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.AMQP.Exchange;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 *  使用交换机 Fount类型进行消息的发送
 *  Fount����
 * @author Administrator
 */
public class FountExchangeSend {
	public static void main(String[] args) throws Exception {
		//创建链接
		Connection connection = ConnectionUtils.connection();
		//创建通道
		Channel channel = connection.createChannel();
		//声明交换机Exchange
		channel.exchangeDeclare(PublicParam.FOUNT_EXCHANGE_NAME,BuiltinExchangeType.FANOUT, false);
		//声明队列
		channel.queueDeclare(PublicParam.FOUNT_QUEUE_NAME1,false, false, false, null);
		channel.queueDeclare(PublicParam.FOUNT_QUEUE_NAME2,false, false, false, null);
		//队列与交换机的绑定
		channel.queueBind(PublicParam.FOUNT_QUEUE_NAME1, PublicParam.FOUNT_EXCHANGE_NAME, "");
		channel.queueBind(PublicParam.FOUNT_QUEUE_NAME2, PublicParam.FOUNT_EXCHANGE_NAME, "");
		//发送消息 下面发送两次表示消息同时发送到两个队列
		channel.basicPublish("", PublicParam.FOUNT_QUEUE_NAME1,null, "测试使用Fount交换机类型发送消息".getBytes());
		channel.basicPublish("", PublicParam.FOUNT_QUEUE_NAME2,null, "测试使用Fount交换机类型发送消息".getBytes());
	}
}
