package com.mq.simple_1;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
/**
 * ����߷�����Ϣ��ͨ
 *  һ������� һ������  һ�������
 * Producer ----- Queue -----Consumer
 * @author Administrator
 *
 */
public class Send {
	
	public static void main(String[] args) throws Exception {
		//��������
		Connection connection = ConnectionUtils.connection();
		//�����ܵ�
		Channel channel = connection.createChannel();
		//��������
		channel.queueDeclare(PublicParam.SIMPLE_QUEUENAME,false, false, false, null);
		//������Ϣ
		for (int i = 0; i < 10; i++) {
			channel.basicPublish("", PublicParam.SIMPLE_QUEUENAME, null,"Simple first Send Msg".getBytes());
		}
		System.err.println("���ͳɹ�!");
	}
}
