package com.mq.transaction.tx;

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
 * 基于事务的消费者 
 * 
 * 			没什么变化
 * @author Administrator
 *
 */
public class TransactionRevc {
	public static void main(String[] args) throws IOException, Exception {
		/*创建链接*/
		Connection connection =  ConnectionUtils.connection();
		/*创建管道*/
		final Channel channel = connection.createChannel();
		/*创建队列*/
		channel.queueDeclare(PublicParam.TRANSACTION_DIRECT_QUEUE, false, false, false, null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.TRANSACTION_DIRECT_EXCHANGENAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		/*交换机与队列进行Binding*/
		channel.queueBind(PublicParam.TRANSACTION_DIRECT_QUEUE, PublicParam.TRANSACTION_DIRECT_EXCHANGENAME, PublicParam.TRANSACTION_DIRECT_EXCHANGE_ROUTINGKEY, null);
		/*每次只读取一条记录*/
		channel.basicQos(1);
		/*监听*/
		channel.basicConsume(PublicParam.TRANSACTION_DIRECT_QUEUE, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				try{
					System.err.println("从队列:["+PublicParam.TRANSACTION_DIRECT_QUEUE+"],获取到的消息结果为：" + new String(body,"UTF-8"));
					//模拟消息处理
					Thread.sleep(3000);
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
				}
			}
		});
	}
}
