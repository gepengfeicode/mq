package com.mq.publishsubscribe;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 *  生产者/发布
 *  
 *      一个生产者对应多个队列
 * @author Administrator
 *
 */
public class PublicshSend {
	public static void main(String[] args) throws Exception, Exception {
		/*创建链接*/
		Connection connection =  ConnectionUtils.connection();
		/*创建管道*/
		Channel channel = connection.createChannel();
		/*创建队列 1*/
		channel.queueDeclare(PublicParam.PUBSUB_FAOUNT_QUEUE_1,false,false,false,null);
		/*创建队列 2*/
		channel.queueDeclare(PublicParam.PUBSUB_FAOUNT_QUEUE_2,false,false,false,null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, BuiltinExchangeType.FANOUT, false, false, null);
		/*队列1 与交换机绑定*/
		channel.queueBind(PublicParam.PUBSUB_FAOUNT_QUEUE_1, PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, PublicParam.PUBSUB_FAOUNT_ROUTINGKEY);
		/*队列2 与交换机绑定*/
		channel.queueBind(PublicParam.PUBSUB_FAOUNT_QUEUE_2, PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, PublicParam.PUBSUB_FAOUNT_ROUTINGKEY);
		/*发布消息*/
		channel.basicPublish(PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, PublicParam.PUBSUB_FAOUNT_ROUTINGKEY, false, false, null, "测试发布订阅".getBytes());
	}
}
