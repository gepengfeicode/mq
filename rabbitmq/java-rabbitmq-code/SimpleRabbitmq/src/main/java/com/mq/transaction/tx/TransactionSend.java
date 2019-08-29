package com.mq.transaction.tx;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 测试 基于事物的消息发送 
 * 	tx select 开启事物
 * @author Administrator
 *
 */
public class TransactionSend {
	public static void main(String[] args) throws IOException, Exception {
		/*创建链接*/
		Connection connection =  ConnectionUtils.connection();
		/*创建管道*/
		Channel channel = connection.createChannel();
		/*创建队列*/
		channel.queueDeclare(PublicParam.TRANSACTION_DIRECT_QUEUE, false, false, false, null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.TRANSACTION_DIRECT_EXCHANGENAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		/*交换机与队列进行Binding*/
		channel.queueBind(PublicParam.TRANSACTION_DIRECT_QUEUE, PublicParam.TRANSACTION_DIRECT_EXCHANGENAME, PublicParam.TRANSACTION_DIRECT_EXCHANGE_ROUTINGKEY, null);
		/*开启事物*/
		channel.txSelect();
		/*发送消息*/
		channel.basicPublish(PublicParam.TRANSACTION_DIRECT_EXCHANGENAME,  PublicParam.TRANSACTION_DIRECT_EXCHANGE_ROUTINGKEY, false, false, null, "测试事务发送消息".getBytes());
		/*提交事物/回滚事物(事务回滚后消息将不会发送到MQ中)*/
//		channel.txCommit();
		channel.txRollback();
		System.err.println("Msg  send  Success");
		System.exit(1);
		
	}
}
