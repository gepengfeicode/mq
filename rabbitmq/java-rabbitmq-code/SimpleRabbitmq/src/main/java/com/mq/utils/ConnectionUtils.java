package com.mq.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionUtils {
	/*IP*/
	private static String HOST = "127.0.0.1";
	/*¶Ë¿Ú*/
	private static int PORT = 5672;
	/*ÐéÄâ¿â*/
	private static String VHOST="/";
	/*ÕËºÅ*/
	private static String USERNAME="guest";
	/*ÃÜÂë*/
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
