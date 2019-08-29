package com.mq.transaction.confirm;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 通过Confirm方式进行发送消息确认
 * @author Administrator
 *
 */
public class ConfirmSend {
	public static void main(String[] args) throws IOException, Exception {
		//普通Confirm模式
		simpleConfirm();
		//批量confirm模式
		msgConfirms();
	}
	//批量的消息确认
	public static void msgConfirms() throws Exception{
		Connection connection = ConnectionUtils.connection();
		
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(PublicParam.CONFIRM_TOPIC_QUEUE, false, false, false, null);
		
		channel.exchangeDeclare(PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, false, false, null);
		
		channel.queueBind(PublicParam.CONFIRM_TOPIC_QUEUE, PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME, PublicParam.CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY + ".#");
		/*开启事物*/
		channel.confirmSelect();
		for (int i = 0; i < 10; i++) {
			channel.basicPublish(PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME,  PublicParam.CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY+ ".1", false, null, "测试Confirm模式下发送消息".getBytes());
		}
		/*判断消息是否成功发送 如果失败抛出Io异常*/
		channel.waitForConfirmsOrDie();
		System.exit(1);
	}
	//普通的Confirm消息确认
	public static void simpleConfirm() throws Exception, Exception{
		Connection connection = ConnectionUtils.connection();
		
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(PublicParam.CONFIRM_TOPIC_QUEUE, false, false, false, null);
		
		channel.exchangeDeclare(PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, false, false, null);
		
		channel.queueBind(PublicParam.CONFIRM_TOPIC_QUEUE, PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME, PublicParam.CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY + ".#");
		/*开启事物*/
		channel.confirmSelect();
		channel.basicPublish(PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME,  PublicParam.CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY+ ".1", false, null, "测试Confirm模式下发送消息".getBytes());

		/*判断消息是否成功发送*/
		String restStr = channel.waitForConfirms()?"成功":"失败";
		System.err.println("消息发送结果为： " + restStr);
		System.exit(1);
	}
}
