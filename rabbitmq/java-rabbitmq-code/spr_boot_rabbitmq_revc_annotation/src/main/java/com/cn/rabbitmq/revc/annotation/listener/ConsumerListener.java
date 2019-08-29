package com.cn.rabbitmq.revc.annotation.listener;

import java.io.UnsupportedEncodingException;

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
import com.rabbitmq.http.client.domain.ExchangeType;
/**
 * 
 * @author Administrator
 * 集成接口后实现omMessage方法 这样就会进行匹配
 *
 */
@Component
public class ConsumerListener implements ChannelAwareMessageListener{
	/**
	 * 各个属性的解释
	 */
	 @RabbitListener(
	 /*containerFactory="rabbitConnectionFactory",*/
	 /*queues = {RabbitPublicParam.SPRTOPICQUEUE},*/
	 exclusive=false,/*是否独占*/
	 priority="",/*优先级*/
	 admin="rabbitadmin",/*rabbitAdmin*/
	 bindings={
	 @QueueBinding(
	 value =
	 @Queue(value=RabbitPublicParam.SPRTOPICQUEUE,arguments={},autoDelete="false",durable="false",exclusive="false")/*声明Queue信息*/,
	 exchange =
	 @Exchange(value=RabbitPublicParam.SPRTOPICEXCHANGENAME,type=ExchangeTypes.TOPIC,arguments={},autoDelete="false",durable="false"),
	 key=RabbitPublicParam.SPRTOPICEXCHANGEROUTINGKEY+".#"
	 )
	 })
	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		// TODO Auto-generated method stub
		 System.err.println("获取到的消息为:" + new String(message.getBody(), "UTF-8"));
	}
}
