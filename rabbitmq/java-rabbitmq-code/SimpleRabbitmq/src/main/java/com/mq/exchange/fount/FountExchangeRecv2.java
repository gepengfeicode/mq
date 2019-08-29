package com.mq.exchange.fount;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class FountExchangeRecv2 {
	public static void main(String[] args) throws Exception {
		//创建链接
		Connection connection = ConnectionUtils.connection();
		//创建通道
		Channel channel = connection.createChannel();
		//声明队列
		channel.queueDeclare(PublicParam.FOUNT_QUEUE_NAME2,false, false, false, null);
		//声明consumer
		Consumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				System.err.println("获取到的消息:" + new String(body,"UTF-8"));
			}
		};
		//进行消息监听
		channel.basicConsume(PublicParam.FOUNT_QUEUE_NAME2, consumer);
		
	}
}
