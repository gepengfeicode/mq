package com.mq.transaction.confirmlistener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 基于Confirm Listener 完成消息确认
 * 
 * @author Administrator
 * 
 */
public class ConfirmListenerSend {
	public static void main(String[] args) throws Exception {
		Connection con = ConnectionUtils.connection();
		
		Channel channel = con.createChannel();
		
		channel.queueDeclare(PublicParam.CONFIRMLISTENER_DIRECT_QUEUE, true, false, false, null);
		
		channel.exchangeDeclare(PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, false, null);
		
		channel.queueBind(PublicParam.CONFIRMLISTENER_DIRECT_QUEUE, PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_NAME, PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_ROUTINGKEY);
		/*开启发送确认*/
		channel.confirmSelect();
		/* mandatory 一定将此设置为true 这样才能使returnlistener生效*/
		channel.basicPublish(PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_NAME,PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_ROUTINGKEY, false,false,null, "基于异步消息确认所发送的消息".getBytes());
		/*在未匹配到队列中调用*/
		channel.addConfirmListener(new ConfirmListener() {
			
			public void handleNack(long deliveryTag, boolean multiple)
					throws IOException {
				// TODO Auto-generated method stub
				System.err.println("消息未被确认" + deliveryTag);
			}
			
			public void handleAck(long deliveryTag, boolean multiple)
					throws IOException {
				// TODO Auto-generated method stub
				System.err.println("消息已经被确认" + deliveryTag);
			}
		});
	}
}
