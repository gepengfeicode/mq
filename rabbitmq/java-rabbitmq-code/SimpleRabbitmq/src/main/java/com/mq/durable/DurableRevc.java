package com.mq.durable;

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
 * 消息持久化消费者
 * @author Administrator
 *
 */
public class DurableRevc {
	public static void main(String[] args) throws IOException, Exception {
		/*创建链接*/
		Connection connection = ConnectionUtils.connection();
		/*创建通道*/
		final Channel channel = connection.createChannel();
		/*创建队列*/
		channel.queueDeclare(PublicParam.DURABLE_QUEU_NAME, true, false, false, null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.DURABLE_DIRECT_EXCHANGE, BuiltinExchangeType.DIRECT, true, false, null);
		/*交换机与队列进行绑定*/
		channel.queueBind(PublicParam.DURABLE_QUEU_NAME, PublicParam.DURABLE_DIRECT_EXCHANGE, PublicParam.DURABLE_DIRECT_ROUTINGKEY,null);
		/*创建消费者对象*/
		channel.basicConsume(PublicParam.DURABLE_QUEU_NAME, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				try{
					System.err.println("从【"+PublicParam.DURABLE_QUEU_NAME+"】中获取到的消息为: " + new String(body,"UTF-8"));                        
					//消息确认
					/*模拟消息处理*/
					Thread.sleep(3000);
					//如果在调用此方法之前关闭服务 那么消息按照未处理返回  消息将继续存在队列中
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					e.printStackTrace();
					/*参数分别为 1.消息标识  2.是否多条消息处理  3.是否重回队列*/
					channel.basicNack(envelope.getDeliveryTag(), false, true);
				}
			}
		});
	}
}
