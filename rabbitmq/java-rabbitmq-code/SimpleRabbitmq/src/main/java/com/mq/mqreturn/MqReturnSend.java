package com.mq.mqreturn;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * Return Listener用于处理一些不可路由的消息
例如:消息生成这通过指定一个Exchange和RoutingKey 把消息送达到某一个队列中去,然后消费者进行监听进行消费处理,但是在某些情况下,如果我们在发送消息的时候,当前的exchange不存在或指定路由Key路由不到,这个时候如果我们需要监听这种不可达的消息,就要使用Return Listener！
 * @author Administrator
 *
 */
public class MqReturnSend {
	public static void main(String[] args) throws Exception, Exception {
		/*创建连接*/
		Connection connection = ConnectionUtils.connection();
		/*创建通道*/
		Channel channel = connection.createChannel();
		/*创建队列*/
		channel.queueDeclare(PublicParam.RETURN_DIRECT_QUEUE, false, false, false, null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.RETURN_DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		/*交换机与队列进行绑定*/
		channel.queueBind(PublicParam.RETURN_DIRECT_QUEUE, PublicParam.RETURN_DIRECT_EXCHANGE_NAME, PublicParam.RETURN_DIRECT_EXCHANGE_ROUTINGKEY);
		/* mandatory 一定将此设置为true 这样才能使returnlistener生效*/
		channel.basicPublish(PublicParam.RETURN_DIRECT_EXCHANGE_NAME, "1", true,false,null, "测试消息未return".getBytes());

		channel.addReturnListener(new ReturnListener() {
			
			public void handleReturn(int arg0, String arg1, String arg2, String arg3,
					BasicProperties arg4, byte[] arg5) throws IOException {
				// TODO Auto-generated method stub
					System.err.println("replyCode " + arg0);
					System.err.println("replyText" + arg1);
					System.err.println("exchange" + arg2);
					System.err.println("routingKey" + arg3);
					System.err.println("消息信息为" + arg4);
					System.err.println("获取到的消息为:" + new String(arg5,"UTF-8"));
			}
		});
	}
}
