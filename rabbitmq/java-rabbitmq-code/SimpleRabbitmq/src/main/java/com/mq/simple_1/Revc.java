package com.mq.simple_1;

import java.io.IOException;

import org.junit.Test;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Basic.Consume;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
/**
 * ����߼�����Ϣ
 * @author Administrator
 *
 */
public class Revc {
//	@Test/*���ȡ*/
	public static void main(String[] args) throws Exception{
		Connection connection = ConnectionUtils.connection();
		Channel channel = connection.createChannel();
		//��������
//		channel.queueDeclare(queueName,false, false, false, null);
		Consumer consume = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				System.err.println("consumerTag " + consumerTag);
				System.out.println("��ȡ������ϢΪ:" + new String(body,"UTF-8"));
			}
		};
		channel.basicConsume(PublicParam.SIMPLE_QUEUENAME,false,consume);
	}
//	@Deprecated
//	@Test/*�Ƚ�©�ķ���*/
//	public void getQueueMsgLoser() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
//		Connection connection = ConnectionUtils.connection();
//		Channel channel = connection.createChannel();
//		QueueingConsumer  consume = new QueueingConsumer(channel);
////		channel.basicConsume(queueName,false,consume);
//		while(true){
//			 QueueingConsumer.Delivery delivery = consume.nextDelivery();
//             System.err.println("�����ConsumerQueue��ȡ������ϢΪ��" + new String(delivery.getBody(),"utf-8"));
//             channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
//		}
//		
//	}
}

