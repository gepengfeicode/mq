package com.mq.worke;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * ����� һ��
 * @author Administrator
 *
 */
public class MqWorkRecv_1 {
	public static void main(String[] args) throws Exception {
		/*��������*/
		Connection connection = ConnectionUtils.connection();
		//����ͨ��
		Channel channel =  connection.createChannel(10);
		//��������
		channel.queueDeclare(PublicParam.WORK_QUEUENAME, false, false, false, null);
		//���������
		Consumer consumer =  new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				System.err.println("获取到的消息为:" + new String(body,"UTF-8"));
			}
		};
		channel.basicConsume(PublicParam.WORK_QUEUENAME, consumer);
	}
}
