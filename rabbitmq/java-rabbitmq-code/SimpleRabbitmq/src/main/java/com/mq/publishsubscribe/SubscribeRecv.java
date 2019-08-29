package com.mq.publishsubscribe;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消费者1 
 * @author Administrator
 *
 */
public class SubscribeRecv {
	public static void main(String[] args) throws Exception, Exception {
		/*创建链接*/
		Connection connection =  ConnectionUtils.connection();
		/*创建管道*/
		final Channel channel = connection.createChannel();
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
		/*消息监听1*/
		channel.basicConsume(PublicParam.PUBSUB_FAOUNT_QUEUE_1,false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				try{
					System.err.println("队列名称:["+PublicParam.PUBSUB_FAOUNT_QUEUE_1+"],获取到的消息为:" + new String(body,"UTF-8"));
					//模拟业务处理
					Thread.sleep(3000);
					//消息确认
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
					// TODO: handle exception
				}
			}
		});
		
		/*消息监听2*/
		channel.basicConsume(PublicParam.PUBSUB_FAOUNT_QUEUE_2,false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				try{
					System.err.println("队列名称:["+PublicParam.PUBSUB_FAOUNT_QUEUE_2+"],获取到的消息为:" + new String(body,"UTF-8"));
					//模拟业务处理
					Thread.sleep(3000);
					//消息确认
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
					// TODO: handle exception
				}
			}
		});
	}
}
