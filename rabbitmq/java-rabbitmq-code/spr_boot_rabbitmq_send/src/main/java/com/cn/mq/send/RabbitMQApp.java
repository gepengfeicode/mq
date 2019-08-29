package com.cn.mq.send;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cn.mq.send"})
public class RabbitMQApp {
	public static void main(String[] args) {
		SpringApplication.run(RabbitMQApp.class, args);
	}
}
