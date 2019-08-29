package com.mq.worke;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * mq工作队列
 * 						  | ---- Consumer  1
 * Producer ----Queue-----
 * 						  | ---- Consumer  2
 * 
 * @author Administrator
 * 
 */
public class MqWorkSend {
	public static void main(String[] args) throws Exception {
		// ��������
		Connection connection = ConnectionUtils.connection();
		// ����ͨ��
		Channel channel = connection.createChannel();
		// ��������
		channel.queueDeclare(PublicParam.WORK_QUEUENAME, false, false, false,null);
		// 循环发送消息
		for (int i = 1; i <= 50; i++) {
			String msg = "msgId: = " + i;
			System.err.println("要发送的消息为:" + msg);
			channel.basicPublish("", PublicParam.WORK_QUEUENAME,null,msg.getBytes());
		}
		channel.close();
		connection.close();
	}
}
