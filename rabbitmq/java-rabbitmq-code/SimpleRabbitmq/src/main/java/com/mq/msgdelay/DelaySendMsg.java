package com.mq.msgdelay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.mq.utils.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 延迟发送消息
 * @author Administrator
 *
 */
public class DelaySendMsg {
	public static final String DELAY_1_QUEUE_NAME="delay_1_queue_name";
	public static final String DELAY_1_EXCHANGE_NAME="delay_1_exchange_name";
	public static final String DLAY_1_EXCHANGE_TYPE=BuiltinExchangeType.DIRECT.getType();
	public static final String DLAY_1_EXCHANGE_ROUTINGKEY="delay_1_exchange_routingkey";
	
	public static final String DELAY_2_QUEUE_NAME="delay_2_queue_name";
	public static final String DELAY_2_EXCHANGE_NAME="delay_2_exchange_name";
	public static final String DLAY_2_EXCHANGE_TYPE=BuiltinExchangeType.DIRECT.getType();
	public static final String DLAY_2_EXCHANGE_ROUTINGKEY="delay_2_exchange_routingkey";
	
	public static void main(String[] args) throws Exception, Exception {
		Connection connection = ConnectionUtils.connection();
		
		Channel channel = connection.createChannel();

		//设置队列信息超时后转接的队列
		Map<String,Object> headers = new HashMap<String, Object>();
		headers.put("x-dead-letter-exchange",DELAY_2_EXCHANGE_NAME);
		headers.put("x-dead-letter-routing-key",DLAY_2_EXCHANGE_ROUTINGKEY);
		//申明第一个队列信息
		channel.queueDeclare(DELAY_1_QUEUE_NAME, false, false, false, headers);
		
		channel.exchangeDeclare(DELAY_1_EXCHANGE_NAME, DLAY_1_EXCHANGE_TYPE, false);
		
		channel.queueBind(DELAY_1_QUEUE_NAME, DELAY_1_EXCHANGE_NAME, DLAY_1_EXCHANGE_ROUTINGKEY);
		
		//申明第二个队列信息
		channel.queueDeclare(DELAY_2_QUEUE_NAME, false, false, false, null);
		
		channel.exchangeDeclare(DELAY_2_EXCHANGE_NAME, DLAY_2_EXCHANGE_TYPE, false);
		
		channel.queueBind(DELAY_2_QUEUE_NAME, DELAY_2_EXCHANGE_NAME, DLAY_2_EXCHANGE_ROUTINGKEY);
		
//		headers.put("x-message-ttl", "300");
//		BasicProperties basicProperties = new BasicProperties().builder().headers(headers).expiration("6000").build();
		 AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().expiration("3000").build();
		channel.basicPublish(DELAY_1_EXCHANGE_NAME, DLAY_1_EXCHANGE_ROUTINGKEY, basicProperties, "发送延迟消息咯".getBytes());
	}
}
