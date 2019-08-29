package com.mq.transaction.confirm;

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
 * Confirm 的消费者
 * @author Administrator
 *
 */
public class ConfirmRevc {
	public static void main(String[] args) throws IOException, Exception {
		Connection con = ConnectionUtils.connection();
		
		final Channel channel = con.createChannel();
		
		channel.queueDeclare(PublicParam.CONFIRM_TOPIC_QUEUE, false, false, false, null);
		
		channel.exchangeDeclare(PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, false, null);
		
		channel.queueBind(PublicParam.CONFIRM_TOPIC_QUEUE, PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME, PublicParam.CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY + ".#");
		
		channel.basicQos(1);
		
		channel.basicConsume(PublicParam.CONFIRM_TOPIC_QUEUE, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stu
				try{
					System.err.println("获取到的消息为:" + new String(body,"UTF-8"));
					channel.basicAck(envelope.getDeliveryTag(), false);
					Thread.sleep(3000);
				}catch (Exception e) {
					channel.basicNack(envelope.getDeliveryTag(), false, false);
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}
}
