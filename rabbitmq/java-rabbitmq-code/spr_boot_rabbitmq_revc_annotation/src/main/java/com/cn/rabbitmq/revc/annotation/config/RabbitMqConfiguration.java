package com.cn.rabbitmq.revc.annotation.config;

import java.util.UUID;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding.DestinationType;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySources;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

@Configuration
@ConfigurationProperties(prefix="spring.rabbitmq")
public class RabbitMqConfiguration {
	private String host;
	private int port;
	private String username;
	private String password;
	private String virtualHost;
	private boolean publisherConfirms;
	private boolean publisherReturns;
	private int connectionTimeout;
	@Value(value="${spring.rabbitmq.Cache.Channel.size}")
	private int channelSize;
	@Bean(value="rabbitadmin")
	public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory){
		System.err.println("================RabbitAdmin=============");
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		rabbitAdmin.setAutoStartup(true);
		System.err.println("================RabbitAdmin======== + End=====");
		return rabbitAdmin;
	}
}
