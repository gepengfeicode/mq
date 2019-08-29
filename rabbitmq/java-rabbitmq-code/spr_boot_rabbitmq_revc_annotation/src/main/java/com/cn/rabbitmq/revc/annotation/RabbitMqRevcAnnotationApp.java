package com.cn.rabbitmq.revc.annotation;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableRabbit
@ComponentScan(basePackages = "com.cn.rabbitmq.revc.annotation")
public class RabbitMqRevcAnnotationApp {
	public static void main(String[] args) {
		SpringApplication.run(RabbitMqRevcAnnotationApp.class, args);
	}
}
