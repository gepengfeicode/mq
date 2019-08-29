package com.mq.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionUtils {
	/*IP*/
	private static String HOST = "127.0.0.1";
	/*�˿�*/
	private static int PORT = 5672;
	/*�����*/
	private static String VHOST="/";
	/*�˺�*/
	private static String USERNAME="guest";
	/*����*/
	private static String PASSWORD="guest";
	public static Connection connection() throws IOException, Exception{
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(HOST);
		connectionFactory.setPort(PORT);
		connectionFactory.setVirtualHost(VHOST);
		connectionFactory.setUsername(USERNAME);
		connectionFactory.setPassword(PASSWORD);
		Connection connection =  connectionFactory.newConnection();
		return connection;
		
	}

}
