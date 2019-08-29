package com.cn.mq.revc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.cn.mq.revc")
public class RabbitMQRevcApp {
	public static void main(String[] args) {
		SpringApplication.run(RabbitMQRevcApp.class, args);
	}
}
