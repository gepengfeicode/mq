package com.cn.rabbitmq.revc.annotation.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import com.cn.rabbitmq.revc.annotation.util.RabbitPublicParam;
import com.rabbitmq.client.Channel;

@Component
public class FountListener implements ChannelAwareMessageListener{
	 @RabbitListener(
			 /*containerFactory="rabbitConnectionFactory",*/
			 /*queues = {RabbitPublicParam.SPRTOPICQUEUE},*/
			 exclusive=false,//�Ƿ��ռ
			 priority="",
			 admin="rabbitadmin",
			 bindings={
			 @QueueBinding(
			 value =
			 @Queue(value=RabbitPublicParam.SPRFOUNTQUEUE,arguments={},autoDelete="false",durable="false",exclusive="false")/*声明Queue信息*/,
			 exchange =
			 @Exchange(value=RabbitPublicParam.SPRFOUNTEXCHANGENAME,type=ExchangeTypes.FANOUT,arguments={},autoDelete="false",durable="false"),
			 key=RabbitPublicParam.SprDirectExchangeRoutingKey
			 )
			 })
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				// TODO Auto-generated method stub
				 System.err.println("获取到的消息为:" + new String(message.getBody(), "UTF-8"));
			}
}
