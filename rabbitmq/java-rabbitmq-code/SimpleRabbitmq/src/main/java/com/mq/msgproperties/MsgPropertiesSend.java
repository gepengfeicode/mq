package com.mq.msgproperties;

import java.io.IOException;
import java.util.HashMap;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 消息生产者
 * 
 *    用于测试消息的属性  例如 优先级 存活时间   自定义头信息 等
 *    
 *    需优先启动消费者 
 *     * @author Administrator
 *
 */
public class MsgPropertiesSend {
	public static void main(String[] args) throws Exception, Exception {
		/*创建连接*/
		Connection connection = ConnectionUtils.connection();
		/*创建通道*/
		Channel channel = connection.createChannel();
		/*创建队列*/
		channel.queueDeclare(PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1, false, false, false, null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.MSGPROPERTIES_DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		/*交换机与队列进行绑定*/
		channel.queueBind(PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1, PublicParam.MSGPROPERTIES_DIRECT_EXCHANGE_NAME, PublicParam.MSGPROPERTIES_DIRECT_ROUTINGKEY);
		HashMap<String, Object> myproperties = new HashMap<String, Object>();
		myproperties.put("title", "head");
		myproperties.put("body", "测试自定义消息");
		/*设置属性*/														 	/*消息是否持久化*/   /*字符編碼*/              /*過期時間*/		  /*優先級*/	
		AMQP.BasicProperties props = new AMQP.BasicProperties().builder().deliveryMode(2).contentEncoding("UTF-8").expiration("6000").priority(1).headers(myproperties).build();
					    
		/*发布消息*/
		channel.basicPublish(PublicParam.MSGPROPERTIES_DIRECT_EXCHANGE_NAME, PublicParam.MSGPROPERTIES_DIRECT_ROUTINGKEY, props, "測試消息屬性".getBytes());
		System.err.println("消息发送完成");

		System.exit(1);
	}
}
