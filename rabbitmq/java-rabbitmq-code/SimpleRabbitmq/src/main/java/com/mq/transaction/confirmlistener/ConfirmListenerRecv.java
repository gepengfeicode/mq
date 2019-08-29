	package com.mq.transaction.confirmlistener;
	
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
	 * 消费者
	 * 
	 * @author Administrator
	 * 
	 */
	public class ConfirmListenerRecv {
		public static void main(String[] args) throws IOException, Exception {
	Connection con = ConnectionUtils.connection();
			
			final Channel channel = con.createChannel();
			
			channel.queueDeclare(PublicParam.CONFIRMLISTENER_DIRECT_QUEUE, true, false, false, null);
			
			channel.exchangeDeclare(PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, false, null);
			
			channel.queueBind(PublicParam.CONFIRMLISTENER_DIRECT_QUEUE, PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_NAME, PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_ROUTINGKEY);
			
			channel.basicConsume(PublicParam.CONFIRMLISTENER_DIRECT_QUEUE, false, new DefaultConsumer(channel){
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope,
						BasicProperties properties, byte[] body) throws IOException {
					try{
						System.err.println("队列名:[" + PublicParam.CONFIRMLISTENER_DIRECT_QUEUE + "],获取到的消息为:" + new String(body,"UTF-8"));
						Thread.sleep(3000);
						channel.basicAck(envelope.getDeliveryTag(), false);
					}catch (Exception e) {
						// TODO: handle exception
						channel.basicNack(envelope.getDeliveryTag(), false, true);
						e.printStackTrace();
					}
				}
			});
		}
	}
