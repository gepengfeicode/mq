package com.mq.ack;

import java.io.IOException;

import javax.security.auth.callback.Callback;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * ack获取消息
 * @author Administrator
 *
 */
public class AckRevc {
	public static void main(String[] args) throws Exception, Exception {
		/*获取连接*/
		Connection con = ConnectionUtils.connection();
		/*获取channel*/
		final Channel channel = con.createChannel();
		/*队列创建*/
		channel.queueDeclare(PublicParam.ACK_QUEUE_NAME, false, false, true, null);
		/*交换机创建*/
		channel.exchangeDeclare(PublicParam.ACK_TOPIC_EXCHANGE, BuiltinExchangeType.TOPIC, false, true, null);
		/*每次只读取一条数据*/
		channel.basicQos(1);
		/*交换机与队列进行绑定*/
		channel.queueBind( PublicParam.ACK_QUEUE_NAME,PublicParam.ACK_TOPIC_EXCHANGE, PublicParam.ACK_ROUTING_KEY + ".#");
		/*监听消息*/
		channel.basicConsume(PublicParam.ACK_QUEUE_NAME,false, new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				try{
					System.err.println("get msg " + new String(body,"UTF-8"));
					try {
						//模拟业务处理
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					/*设置消息确认*/
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					System.err.println("系统异常了,异常信息" + e.getMessage());
					//requeue 是否重回队列
					channel.basicNack(envelope.getDeliveryTag(), false, true);
				}
			}
		});
		
	}
}
