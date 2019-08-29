package com.mq.msgproperties;

import java.io.IOException;
import java.util.Map;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消费者
 * @author Administrator
 *
 */
public class MsgPropertiesRevc {
	public static void main(String[] args) throws IOException, Exception {
		/*创建连接*/
		Connection connection = ConnectionUtils.connection();
		/*创建通道*/
		final Channel channel = connection.createChannel();
		/*创建队列*/
		channel.queueDeclare(PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1, false, false, false, null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.MSGPROPERTIES_DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		/*交换机与队列进行绑定*/
		channel.queueBind(PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1, PublicParam.MSGPROPERTIES_DIRECT_EXCHANGE_NAME, PublicParam.MSGPROPERTIES_DIRECT_ROUTINGKEY);
		/*获取消息*/
		channel.basicConsume(PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				try{
					System.err.println("队列名称:["+PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1+"]获取到的消息为" + new String(body,"UTF-8"));
					Map<String, Object> heads = properties.getHeaders();
					System.err.println("队列名称:["+PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1+"]获取到的额外信息为" + heads.toString());
					Thread.sleep(3000);//模拟业务处理
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
				}
			}
		});
	}
}
