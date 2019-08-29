# RabbitMQ

[TOC]



## 概述

```xml-dtd
RabbitMQ的开发语言以及支持的语言
RabbitMq是由ErLang语言开发的基于AMQP(Advanced Message Queue)协议的开源工具,
RabbitMQ支持的语言有 C# C JavaScript Java PHP 等
```

### RabbitMQ执行流程

生产者发送消息到Exchange(交换机)由RoutingKey匹配具体的交换机,由交换机的BingdingKey进行消息的发送,发送到指定队列中,由消费者订阅读取。

消费者发消息 ---> 发送指定 --->  MQ服务 ---> 指定虚拟主机  ---> 指定交换机 --->进入消息队列 -->消费者读取 

下图为架构图

![](C:\Users\Administrator\Desktop\xi\rabbitmq\images\rabbitmq消息发送及消费的流程图.png)

### 一次消息发送全流程

生产者发送消息--->匹配交换机---->放入到订阅的队列中---->消费者进行读取消费

![](C:\Users\Administrator\Desktop\xi\rabbitmq\images\mq一次发送消息的流程.png)



### RabbitMQ架构图

![](C:\Users\Administrator\Desktop\xi\rabbitmq\images\RabbitMQ架构图.png)

名词解释：

​	1.Send Message 可以理解为生产者 Producer 用于发送消息

​	2.生产者发送消息通过rountKey进行匹配

​		根据交换机的类型不同routingKey的匹配规则不同常用的有(Topic,Direct,Fount)下面会详细说

​	3.路由到交换机后由交换机放入到订阅的队列中

​	4.Recevie Message消费者订阅队列进行消息的消息

### 交换机

**生产者产生的消息并不是直接发给消息队列的的,而是经过Exchange**

**由Exchange再将消息路由到一个或多个Queue，当然这里还会对不符合路由规则的消息进行丢弃掉，**

**那么Exchange是怎样将消息准确的推送到对应的Queue的呢？那么这里的功劳最大的当属Binding，RabbitMQ是通过Binding将Exchange和Queue链接在一起，这样Exchange就知道如何将消息准确的推送到Queue中去。简单示意图如下所示：**、

![](C:\Users\Administrator\Desktop\xi\rabbitmq\images\Exchange示例图.png)

**在绑定（Binding）Exchange和Queue的同时，一般会指定一个Binding Key，生产者将消息发送给Exchange的时候，一般会产生一个Routing Key，当Routing Key和Binding Key对应上的时候，消息就会发送到对应的Queue中去。那么Exchange有四种类型，不同的类型有着不同的策略。也就是表明不同的类型将决定绑定的Queue不同，换言之就是说生产者发送了一个消息，Routing Key的规则是A，那么生产者会将Routing Key=A的消息推送到Exchange中，这时候Exchange中会有自己的规则，对应的规则去筛选生产者发来的消息，如果能够对应上Exchange的内部规则就将消息推送到对应的Queue中去。**

## RabbitMQ的简单使用



### 1.工具类

```java
package com.cn.send.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.org.apache.regexp.internal.recompile;
/**
 * 工具类主要用于创建连接
 * @author Administrator
 *
 */
@Service
public class MQConnection {
	//登录账号
	private static String userName = "guest";
	//登录密码
	private static String passWord = "guest";
	//端口
	private static int port = 5672;
	//虚拟库
	private static String vhost = "/";
	
	
	/*
	 * 创建连接
	 */
	public static Connection connectionFactory() throws Exception, Exception{
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setUsername(userName);
		connectionFactory.setPassword(passWord);
		connectionFactory.setVirtualHost(vhost);
		connectionFactory.setPort(port);
		System.err.println("Mq ConnectionFactory Success!");
		return connectionFactory.newConnection();
	}
}

package com.cn.send.utils;
/**
 * 公共参数声明
 * @author Administrator
 *
 */
public class MQParam {
	//简单队列
	public static final String SIMPLE_QUEUE_NAME = "simple_queue_name";
}

```

### 2.生产者



```java
package com.cn.send.simple;

import java.io.IOException;

import com.cn.send.utils.MQConnection;
import com.cn.send.utils.MQParam;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * RabbitMQ 简单的使用   
 *   生产者发送消息
 *   生产者                      队列                     消费者
 *   producer ---  Queue   --- consumer
 *   
 *   
 * @author Administrator
 *
 */
public class SimpleSendMsg {
	public static void main(String[] args) throws Exception {
		sendSimpleMsg();
	}
	/**
	 * 发送简单消息
	 * @throws Exception 
	 */
	public static void sendSimpleMsg() throws Exception{
		//创建链接
		Connection connection = MQConnection.connectionFactory();
		//创建通道
		Channel channel = connection.createChannel();
		/**
		 * 创建队列 、
		 * 对应的参数列表如下 ：　
		 * durable   是否持久化  不持久的话第二天数据信息清楚
		 * exclusive 是否独占   如果独占的话标识该队列只能对应一个消费者
		 * autoDelete 是否自动删除 当队列内无数据的时候
		 * arguments  队列属性
		 */
		channel.queueDeclare(MQParam.SIMPLE_QUEUE_NAME, false, false, true, null);
		//发送消息  第二个参数为队列名称 
		channel.basicPublish("", MQParam.SIMPLE_QUEUE_NAME, null, "第一次发送测试消息·".getBytes());
		System.err.println("发送成功");
	}
}

```

### 3.消费者



```java
package com.cn.send.simple;

import java.io.IOException;

import com.cn.send.utils.MQConnection;
import com.cn.send.utils.MQParam;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消息的接受者
 * @author Administrator
 *
 */
public class SimpleRevcMsg {
	public static void main(String[] args) throws Exception {
		revcSimpleQueue();
	}
	/**
	 * 消息的接受者
	 * @throws Exception 
	 */
	public static void revcSimpleQueue() throws Exception{
		//創建連接
		Connection con = MQConnection.connectionFactory();
		//创建管道
		final Channel channel = con.createChannel();
		/**
		 * 创建队列 、
		 * 对应的参数列表如下 ：　
		 * durable   是否持久化  不持久的话第二天数据信息清楚
		 * exclusive 是否独占   如果独占的话标识该队列只能对应一个消费者
		 * autoDelete 是否自动删除 当队列内无数据的时候
		 * arguments  队列属性
		 */
		channel.queueDeclare(MQParam.SIMPLE_QUEUE_NAME,false, false, true, null);
		//创建消费者对象
		channel.basicConsume(MQParam.SIMPLE_QUEUE_NAME,false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				try {
					System.out.println("获取到的消息为：" + new String(body,"UTF-8"));
					Thread.sleep(300);//三秒后业务处理完成
					channel.basicAck(envelope.getDeliveryTag(), false);
				} catch (Exception e) {
					//消息确认失败                        multiple 是否多条处理             requeue 是否重回队列       
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
					// TODO: handle exception
				}
			}
		});
	}
}

```

## RabbitMQ Topic类型交换机

### 1.概述

**topic这个规则就是模糊匹配，可以通过通配符满足一部分规则就可以传送。它的约定是：**

**1.routing key为一个句点号“. ”分隔的字符串（我们将被句点号“. ”分隔开的每一段独立的字符串称为一个单词），如“stock.usd.nyse”、“nyse.vmw”、“quick.orange.rabbit”**

**2.binding key与routing key一样也是句点号“. ”分隔的字符串**

**3.binding key中可以存在两种特殊字符“\*”与“#”，用于做模糊匹配，其中“\*”用于匹配一个单词，“#”用于匹配多个单词（可以是零个）**

**Topic消息发送的流程**

![topic消息发送的流程](C:\Users\Administrator\Desktop\xi\rabbitmq\images\topic消息发送流程.png)

### 2.示例代码

#### 1.工具类

```java
package com.cn.send.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.org.apache.regexp.internal.recompile;
/**
 * 工具类主要用于创建连接
 * @author Administrator
 *
 */
public class MQConnection {
	//登录账号
	private static String userName = "guest";
	//登录密码
	private static String passWord = "guest";
	//端口
	private static int port = 5672;
	//虚拟库
	private static String vhost = "/";
	
	
	/*
	 * 创建连接
	 */
	public static Connection connectionFactory() throws Exception, Exception{
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setUsername(userName);
		connectionFactory.setPassword(passWord);
		connectionFactory.setVirtualHost(vhost);
		connectionFactory.setPort(port);
		System.err.println("Mq ConnectionFactory Success!");
		return connectionFactory.newConnection();
	}
}
package com.cn.send.utils;
/**
 * 公共参数声明
 * @author Administrator
 *
 */
public class MQParam {
	//简单队列
	public static final String SIMPLE_QUEUE_NAME = "simple_queue_name";
	
	
	//topic队列相关参数
	public static final String TOPIC_QUEUE_NAME = "topic_queue_name";
	public static final String TOPIC_EXCHANGE_NAME="topic_exchange_name";
	public static final String TOPIC_EXCHANGE_ROUTINGKEY="topic_exchange_routingKey.#";
}

```

#### 2.消息生产者

```java
package com.cn.send.topic;

import com.cn.send.utils.MQConnection;
import com.cn.send.utils.MQParam;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.BuiltinExchangeType;
/**
 * 使用Tpic类型交换机进行消息的发送
 * tpic这个规则就是模糊匹配，可以通过通配符满足一部分规则就可以传送。它的约定是：
   1.routing key为一个句点号“. ”分隔的字符串（我们将被句点号“. ”分隔开的每一段独立的字符串称为一个单词），如“stock.usd.nyse”、“nyse.vmw”、“quick.orange.rabbit”
   2.binding key与routing key一样也是句点号“. ”分隔的字符串
   3.binding key中可以存在两种特殊字符“*”与“#”，用于做模糊匹配，其中“*”用于匹配一个单词，“#”用于匹配多个单词（可以是零个）
 * 
 * @author Administrator
 *
 */
public class SimpleTopicSendMsg {
	public static void main(String[] args) throws Exception {
		//创建链接
		Connection con = MQConnection.connectionFactory();
		//创建通道
		Channel channel = con.createChannel();
		//声明队列
		/*
		 * 参数1   队列名称
		 * 参数2  是否持久化          持久化后队列以及队列的信息将一致存储至MQ中
		 * 参数3  是否独占	        开启后 只允许一个消费者进行消费
		 * 参数4  是否自动删除       当队列内没有数据后是否自动删除队列
		 * 参数5  自定义属性
		 * */
		channel.queueDeclare(MQParam.TOPIC_QUEUE_NAME, false, false, false, null);
		//声明交换机
		/**
		 * 参数列表
		 *   1. 交换机名称
		 *   2. 交换机类型 
		 *   3. 是否持久化 
		 *   4. 是否自动删除
		 *   5. ########
		 *   6. 交换机属性
		 */
		channel.exchangeDeclare(MQParam.TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, false, false, null);
		//队列与交换机进行绑定
		/**
		 * 参数列表
		 *  1. 队列名称
		 *  2. 交换机名称
		 *  3. routingKey  匹配的字符串
		 */
		channel.queueBind(MQParam.TOPIC_QUEUE_NAME, MQParam.TOPIC_EXCHANGE_NAME, MQParam.TOPIC_EXCHANGE_ROUTINGKEY);
		/**
		 * 消息的发送
		 *  参数列表
		 *   1. 交换机名称
		 *   2.routing Key
		 *   3.消息属性
		 *   4.发送的消息
		 */  
		channel.basicPublish(MQParam.TOPIC_EXCHANGE_NAME, MQParam.TOPIC_EXCHANGE_ROUTINGKEY+"1", null, "发送Topic类型消息".getBytes());
	}
}

```

#### 3 消息消费者

```java
package com.cn.send.topic;

import java.awt.Event;
import java.io.IOException;

import com.cn.send.utils.MQConnection;
import com.cn.send.utils.MQParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * Topic的消息消费者
 * @author Administrator
 */
public class SimpleTopicRevcMsg {
	public static void main(String[] args) throws Exception {
		//创建链接
		Connection con = MQConnection.connectionFactory();
		//创建通道
		final Channel channel = con.createChannel();
		//声明队列
		/*
		 * 参数1   队列名称
		 * 参数2  是否持久化          持久化后队列以及队列的信息将一致存储至MQ中
		 * 参数3  是否独占	        开启后 只允许一个消费者进行消费
		 * 参数4  是否自动删除       当队列内没有数据后是否自动删除队列
		 * 参数5  自定义属性
		 * */
		channel.queueDeclare(MQParam.TOPIC_QUEUE_NAME, false, false, false, null);
		//声明交换机
		/**
		 * 参数列表
		 *   1. 交换机名称
		 *   2. 交换机类型 
		 *   3. 是否持久化 
		 *   4. 是否自动删除
		 *   5. ########
		 *   6. 交换机属性
		 */
		channel.exchangeDeclare(MQParam.TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, false, false, null);
		//队列与交换机进行绑定
		/**
		 * 参数列表
		 *  1. 队列名称
		 *  2. 交换机名称
		 *  3. routingKey  匹配的字符串
		 */
		channel.queueBind(MQParam.TOPIC_QUEUE_NAME, MQParam.TOPIC_EXCHANGE_NAME, MQParam.TOPIC_EXCHANGE_ROUTINGKEY);
		//监听消费   
		/**
		 * 参数列表对应为
		 *  队列名称
		 *  消息自动应答   true的话不需要ack的操作 false需要手动确认
		 *  消息监听的对象
		 */
		channel.basicConsume(MQParam.TOPIC_QUEUE_NAME, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				try {
					System.err.println("获取到的消息为：" + new String(body,"UTF-8"));
					//模拟业务处理
					Thread.sleep(3000);
					//消息确认
					/**
					 * 参数列表 1.消息ID   2.是否多条处理
					 */
					channel.basicAck(envelope.getDeliveryTag(), false);
				} catch (InterruptedException e) {
					/**
					 * 参数列表 1.消息ID   2.是否多条处理 3.消息是否重回队列
					 */
					channel.basicNack(envelope.getDeliveryTag(), false,false);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
}

```

## RabbitMQ Found类型交换机

### 1.概述

**fanout类型的Exchange路由规则非常简单，它会把所有发送到该Exchange的消息路由到所有与它绑定的Queue中。没有RoutaingKey这一说,默认传空**

![](C:\Users\Administrator\Desktop\xi\rabbitmq\images\found交换机的流程图.png)

### 2.示例代码

#### 1.工具类

```java
package com.cn.send.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.org.apache.regexp.internal.recompile;
/**
 * 工具类主要用于创建连接
 * @author Administrator
 *
 */
public class MQConnection {
	//登录账号
	private static String userName = "guest";
	//登录密码
	private static String passWord = "guest";
	//端口
	private static int port = 5672;
	//虚拟库
	private static String vhost = "/";
	
	
	/*
	 * 创建连接
	 */
	public static Connection connectionFactory() throws Exception, Exception{
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setUsername(userName);
		connectionFactory.setPassword(passWord);
		connectionFactory.setVirtualHost(vhost);
		connectionFactory.setPort(port);
		System.err.println("Mq ConnectionFactory Success!");
		return connectionFactory.newConnection();
	}
}
package com.cn.send.utils;
/**
 * 公共参数声明
 * @author Administrator
 *
 */
public class MQParam {
	//简单队列
	public static final String SIMPLE_QUEUE_NAME = "simple_queue_name";
	
	
	//topic交换机队列相关参数
	public static final String TOPIC_QUEUE_NAME = "topic_queue_name";
	public static final String TOPIC_EXCHANGE_NAME="topic_exchange_name";
	public static final String TOPIC_EXCHANGE_ROUTINGKEY="topic_exchange_routingKey.#";
	
	
	//found交换机队列相关参数
	public static final String FOUND_QUEUE_NAME="foud_queue_name";
	public static final String FOUND_EXCHANGE_NAME="found_exchange_name";
	public static final String FOUND_EXCHANGE_ROUTINGKEY="";
}

```

#### 2.生产者

```java
package com.cn.send.found;

import com.cn.send.utils.MQConnection;
import com.cn.send.utils.MQParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class SimpleFoundSendMsg {
	public static void main(String[] args) throws Exception {
		//创建链接
		Connection con = MQConnection.connectionFactory();
		//创建通道
		Channel channel = con.createChannel();
		//声明队列
		/*
		 * 参数1   队列名称
		 * 参数2  是否持久化          持久化后队列以及队列的信息将一致存储至MQ中
		 * 参数3  是否独占	        开启后 只允许一个消费者进行消费
		 * 参数4  是否自动删除       当队列内没有数据后是否自动删除队列
		 * 参数5  自定义属性
		 * */
		channel.queueDeclare(MQParam.FOUND_QUEUE_NAME, false, false, false, null);
		//声明交换机
		/**
		 * 参数列表
		 *   1. 交换机名称
		 *   2. 交换机类型 
		 *   3. 是否持久化 
		 *   4. 是否自动删除
		 *   5. ########
		 *   6. 交换机属性
		 */
		channel.exchangeDeclare(MQParam.FOUND_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, false, false, null);
		//队列与交换机进行绑定
		/**
		 * 参数列表
		 *  1. 队列名称
		 *  2. 交换机名称
		 *  3. routingKey  匹配的字符串
		 */
		channel.queueBind(MQParam.FOUND_QUEUE_NAME, MQParam.FOUND_EXCHANGE_NAME, MQParam.FOUND_EXCHANGE_ROUTINGKEY);
		/**
		 * 消息的发送
		 *  参数列表
		 *   1. 交换机名称
		 *   2.routing Key
		 *   3.消息属性
		 *   4.发送的消息
		 */  
		channel.basicPublish(MQParam.FOUND_EXCHANGE_NAME, "", null, "发送Found类型消息".getBytes());
	}
}

```

#### 3.消费者

 

```java
package com.cn.send.found;

import java.awt.Event;
import java.io.IOException;

import com.cn.send.utils.MQConnection;
import com.cn.send.utils.MQParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * Topic的消息消费者
 * @author Administrator
 */
public class SimpleFoundRevcMsg {
	public static void main(String[] args) throws Exception {
		//创建链接
		Connection con = MQConnection.connectionFactory();
		//创建通道
		final Channel channel = con.createChannel();
		//声明队列
		/*
		 * 参数1   队列名称
		 * 参数2  是否持久化          持久化后队列以及队列的信息将一致存储至MQ中
		 * 参数3  是否独占	        开启后 只允许一个消费者进行消费
		 * 参数4  是否自动删除       当队列内没有数据后是否自动删除队列
		 * 参数5  自定义属性
		 * */
		channel.queueDeclare(MQParam.FOUND_QUEUE_NAME, false, false, false, null);
		//声明交换机
		/**
		 * 参数列表
		 *   1. 交换机名称
		 *   2. 交换机类型 
		 *   3. 是否持久化 
		 *   4. 是否自动删除
		 *   5. ########
		 *   6. 交换机属性
		 */
		channel.exchangeDeclare(MQParam.FOUND_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, false, false, null);
		//队列与交换机进行绑定
		/**
		 * 参数列表
		 *  1. 队列名称
		 *  2. 交换机名称
		 *  3. routingKey  匹配的字符串
		 */
		channel.queueBind(MQParam.FOUND_QUEUE_NAME, MQParam.FOUND_EXCHANGE_NAME, MQParam.FOUND_EXCHANGE_ROUTINGKEY);
		//监听消费   
		/**
		 * 参数列表对应为
		 *  队列名称
		 *  消息自动应答   true的话不需要ack的操作 false需要手动确认
		 *  消息监听的对象
		 */
		channel.basicConsume(MQParam.FOUND_QUEUE_NAME, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				try {
					System.err.println("获取到的消息为：" + new String(body,"UTF-8"));
					//模拟业务处理
					Thread.sleep(3000);
					//消息确认
					/**
					 * 参数列表 1.消息ID   2.是否多条处理
					 */
					channel.basicAck(envelope.getDeliveryTag(), false);
				} catch (InterruptedException e) {
					/**
					 * 参数列表 1.消息ID   2.是否多条处理 3.消息是否重回队列
					 */
					channel.basicNack(envelope.getDeliveryTag(), false,false);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		System.err.println("消费者启动成功");
	}
}
```



## RabbitMQ Direct类型交换机

### 1.概述



**direct类型的Exchange路由规则也很简单，它会把消息路由到那些binding key与routing key完全匹配的Queue中**

**当生产者（P）发送消息时Rotuing key=booking时，这时候将消息传送给Exchange，Exchange获取到生产者发送过来消息后，会根据自身的规则进行与匹配相应的Queue，这时发现Queue1和Queue2都符合，就会将消息传送给这两个队列，如果我们以Rotuing key=create和Rotuing key=confirm发送消息时，这时消息只会被推送到Queue2队列中，其他Routing Key的消息将会被丢弃。**

![](C:\Users\Administrator\Desktop\xi\rabbitmq\images\direct交换机发送消息流程.png)





### 2.工具类

```java
package com.cn.send.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.org.apache.regexp.internal.recompile;
/**
 * 工具类主要用于创建连接
 * @author Administrator
 *
 */
public class MQConnection {
	//登录账号
	private static String userName = "guest";
	//登录密码
	private static String passWord = "guest";
	//端口
	private static int port = 5672;
	//虚拟库
	private static String vhost = "/";
	
	
	/*
	 * 创建连接
	 */
	public static Connection connectionFactory() throws Exception, Exception{
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setUsername(userName);
		connectionFactory.setPassword(passWord);
		connectionFactory.setVirtualHost(vhost);
		connectionFactory.setPort(port);
		System.err.println("Mq ConnectionFactory Success!");
		return connectionFactory.newConnection();
	}
}

package com.cn.send.utils;
/**
 * 公共参数声明
 * @author Administrator
 *
 */
public class MQParam {
	//简单队列
	public static final String SIMPLE_QUEUE_NAME = "simple_queue_name";
	
	
	//topic交换机队列相关参数
	public static final String TOPIC_QUEUE_NAME = "topic_queue_name";
	public static final String TOPIC_EXCHANGE_NAME="topic_exchange_name";
	public static final String TOPIC_EXCHANGE_ROUTINGKEY="topic_exchange_routingKey.#";
	
	
	//found交换机队列相关参数
	public static final String FOUND_QUEUE_NAME="found_queue_name";
	public static final String FOUND_EXCHANGE_NAME="found_exchange_name";
	public static final String FOUND_EXCHANGE_ROUTINGKEY="";
	
	//direct类型交换机相关参数
	public static final String DIRECT_QUEUE_NAME="direct_queue_name";
	public static final String DIRECT_EXCHANGE_NAME="direct_exchange_name";
	public static final String DIRECT_EXCHANGE_ROUTINGKEY="direct_exchange_routingKey";
}

```







### 3.生产者



```java
package com.cn.send.direct;

import java.awt.Event;
import java.io.IOException;

import com.cn.send.utils.MQConnection;
import com.cn.send.utils.MQParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * Topic的消息消费者
 * @author Administrator
 */
public class SimpleDirectSendMsg {
	public static void main(String[] args) throws Exception {
		//创建链接
		Connection con = MQConnection.connectionFactory();
		//创建通道
		final Channel channel = con.createChannel();
		//声明队列
		/*
		 * 参数1   队列名称
		 * 参数2  是否持久化          持久化后队列以及队列的信息将一致存储至MQ中
		 * 参数3  是否独占	        开启后 只允许一个消费者进行消费
		 * 参数4  是否自动删除       当队列内没有数据后是否自动删除队列
		 * 参数5  自定义属性
		 * */
		channel.queueDeclare(MQParam.DIRECT_QUEUE_NAME, false, false, false, null);
		//声明交换机
		/**
		 * 参数列表
		 *   1. 交换机名称
		 *   2. 交换机类型 
		 *   3. 是否持久化 
		 *   4. 是否自动删除
		 *   5. ########
		 *   6. 交换机属性
		 */
		channel.exchangeDeclare(MQParam.DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		//队列与交换机进行绑定
		/**
		 * 参数列表
		 *  1. 队列名称
		 *  2. 交换机名称
		 *  3. routingKey  匹配的字符串
		 */
		channel.queueBind(MQParam.DIRECT_QUEUE_NAME, MQParam.DIRECT_EXCHANGE_NAME, MQParam.DIRECT_EXCHANGE_ROUTINGKEY);
		/**
		 * 消息的发送
		 *  参数列表
		 *   1. 交换机名称
		 *   2.routing Key
		 *   3.消息属性
		 *   4.发送的消息
		 */  
		channel.basicPublish(MQParam.DIRECT_EXCHANGE_NAME, MQParam.DIRECT_EXCHANGE_ROUTINGKEY, null, "发送Direct类型消息".getBytes());
		System.out.println("消息发送成功");
		System.exit(1);
	}
}
```



### 4.消费者

```java
package com.cn.send.direct;

import java.awt.Event;
import java.io.IOException;

import com.cn.send.utils.MQConnection;
import com.cn.send.utils.MQParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * Topic的消息消费者
 * @author Administrator
 */
public class SimpleDirectRevcMsg {
	public static void main(String[] args) throws Exception {
		//创建链接
		Connection con = MQConnection.connectionFactory();
		//创建通道
		final Channel channel = con.createChannel();
		//声明队列
		/*
		 * 参数1   队列名称
		 * 参数2  是否持久化          持久化后队列以及队列的信息将一致存储至MQ中
		 * 参数3  是否独占	        开启后 只允许一个消费者进行消费
		 * 参数4  是否自动删除       当队列内没有数据后是否自动删除队列
		 * 参数5  自定义属性
		 * */
		channel.queueDeclare(MQParam.DIRECT_QUEUE_NAME, false, false, false, null);
		//声明交换机
		/**
		 * 参数列表
		 *   1. 交换机名称
		 *   2. 交换机类型 
		 *   3. 是否持久化 
		 *   4. 是否自动删除
		 *   5. ########
		 *   6. 交换机属性
		 */
		channel.exchangeDeclare(MQParam.DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		//队列与交换机进行绑定
		/**
		 * 参数列表
		 *  1. 队列名称
		 *  2. 交换机名称
		 *  3. routingKey  匹配的字符串
		 */
		channel.queueBind(MQParam.DIRECT_QUEUE_NAME, MQParam.DIRECT_EXCHANGE_NAME, MQParam.DIRECT_EXCHANGE_ROUTINGKEY);
		//监听消费   
		/**
		 * 参数列表对应为
		 *  队列名称
		 *  消息自动应答   true的话不需要ack的操作 false需要手动确认
		 *  消息监听的对象
		 */
		channel.basicConsume(MQParam.DIRECT_QUEUE_NAME, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				try {
					System.err.println("获取到的消息为：" + new String(body,"UTF-8"));
					//模拟业务处理
					Thread.sleep(3000);
					//消息确认
					/**
					 * 参数列表 1.消息ID   2.是否多条处理
					 */
					channel.basicAck(envelope.getDeliveryTag(), false);
				} catch (InterruptedException e) {
					/**
					 * 参数列表 1.消息ID   2.是否多条处理 3.消息是否重回队列
					 */
					channel.basicNack(envelope.getDeliveryTag(), false,false);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		System.err.println("消费者启动成功");
	}
}

```

## 消息确认代码

### 1.概述

- [ ] ```java
  执行任务可能需要几秒钟。你可能想知道如果其中一个消费者开始一项长期任务并且只是部分完成而死亡会发生什么。使用我们当前的代码，一旦RabbitMQ向消费者发送消息，它立即将其标记为删除。在这种情况下，如果你杀死一个工人，我们将丢失它刚刚处理的消息。我们还将丢失分发给这个特定工作者但尚未处理的所有消息。
  
  但我们不想失去任何任务。如果工人死亡，我们希望将任务交付给另一名工人。
  
  为了确保消息永不丢失，RabbitMQ支持 消息确认。消费者发回ack（nowledgement）告诉RabbitMQ已收到，处理了特定消息，RabbitMQ可以自由删除它。
  
  如果消费者死亡（其通道关闭，连接关闭或TCP连接丢失）而不发送确认，RabbitMQ将理解消息未完全处理并将重新排队。如果同时有其他在线消费者，则会迅速将其重新发送给其他消费者。这样你就可以确保没有消息丢失，即使工人偶尔会死亡。
  
  没有任何消息超时; 当消费者死亡时，RabbitMQ将重新发送消息。即使处理消息需要非常长的时间，也没关系。
  
  **使用消息确认一定要在Finally进行手动确认否则会导致消息挤压导致内存增加导致服务宕机。**
  
  要实现手动消息确认需要两个条件
  
  1. 需要将Consumer端的自动应答设置为False
  
  2. 需要调用channel.basicack()/channel.basicnack()方法实现
  
  ack作用在消费端    重点
  
  channel.basicack(int,boolean)
  channel.basicAck(envelope.getDeliveryTag(),false);
  消息确认后会从队列中删除 参数1 消息编号 参数2 是否多条处理
  channel.basicnack()
  //参数列表 1  消息ID       2 是否批量   3 是否重回队列 true 回  false 不回消息直接删除
  channel.basicNack(envelope.getDeliveryTag(),false,false);
  ```

  ![简单示例图](C:\Users\Administrator\Desktop\xi\rabbitmq\images\消息确认机制示例图.png)



### 2.工具类

```java
package com.cn.send.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.org.apache.regexp.internal.recompile;
/**
 * 工具类主要用于创建连接
 * @author Administrator
 *
 */
public class MQConnection {
	//登录账号
	private static String userName = "guest";
	//登录密码
	private static String passWord = "guest";
	//端口
	private static int port = 5672;
	//虚拟库
	private static String vhost = "/";
	
	
	/*
	 * 创建连接
	 */
	public static Connection connectionFactory() throws Exception, Exception{
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setUsername(userName);
		connectionFactory.setPassword(passWord);
		connectionFactory.setVirtualHost(vhost);
		connectionFactory.setPort(port);
		System.err.println("Mq ConnectionFactory Success!");
		return connectionFactory.newConnection();
	}
}
package com.cn.send.utils;
/**
 * 公共参数声明
 * @author Administrator
 *
 */
public class MQParam {
	//简单队列
	public static final String SIMPLE_QUEUE_NAME = "simple_queue_name";
	
	
	//topic交换机队列相关参数
	public static final String TOPIC_QUEUE_NAME = "topic_queue_name";
	public static final String TOPIC_EXCHANGE_NAME="topic_exchange_name";
	public static final String TOPIC_EXCHANGE_ROUTINGKEY="topic_exchange_routingKey.#";
	
	
	//found交换机队列相关参数
	public static final String FOUND_QUEUE_NAME="found_queue_name";
	public static final String FOUND_EXCHANGE_NAME="found_exchange_name";
	public static final String FOUND_EXCHANGE_ROUTINGKEY="";
	
	//direct类型交换机相关参数
	public static final String DIRECT_QUEUE_NAME="direct_queue_name";
	public static final String DIRECT_EXCHANGE_NAME="direct_exchange_name";
	public static final String DIRECT_EXCHANGE_ROUTINGKEY="direct_exchange_routingKey";
	
	//用于测试ACK的实现
	public static final String ACK_QUEUE_NAME= "ack_queue_name";
	public static final String ACK_EXCHANGE_NAME="ack_exchange_name"; 
	public static final String ACK_EXCHANGE_ROUTINGKEY="ack_exchange_routingKey";
}

```



### 3.生产者



```java
package com.cn.send.ack;

import com.cn.send.utils.MQConnection;
import com.cn.send.utils.MQParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 消息确认
 *   消息确认是通过channel的ack方法实现的主要用于消费者端
 *   
 *   
 * @author Administrator
 *
 */
public class SendMsgAck {
	public static void main(String[] args) throws Exception {
		/**
		 * 1.创建链接
		 * 2.创建channel
		 * 3.声明队列
		 * 4.声明交换机
		 * 5.队列与交换机进行绑定
		 * 6.发送消息
		 */
		
		Connection connection = MQConnection.connectionFactory();
		
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(MQParam.ACK_QUEUE_NAME, false, false, false, null);
		
		channel.exchangeDeclare(MQParam.ACK_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		
		channel.queueBind(MQParam.ACK_QUEUE_NAME,MQParam.ACK_EXCHANGE_NAME, MQParam.ACK_EXCHANGE_ROUTINGKEY);
		
		for (int i = 0; i < 10; i++) {
			channel.basicPublish(MQParam.ACK_EXCHANGE_NAME, MQParam.ACK_EXCHANGE_ROUTINGKEY, null, "测试使用ack进行消息确认".getBytes());
		}
		System.err.println("消息发送成功！！！！");
		System.exit(0);
	}
}

```

### 4.消费者

```java
package com.cn.send.ack;

import java.io.IOException;

import com.cn.send.utils.MQConnection;
import com.cn.send.utils.MQParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消息确认
 *   消息确认是通过channel的ack方法实现的主要用于消费者端
 *   将multiple 设置为true的时候多条消息同时进行确认 最好设置为false
 *   qps设置为1 是保证消息不被独享  保证消息可以多个消费者同时进行消费
 *   
 * @author Administrator
 *
 */
public class RevcMsgAck {
	public static void main(String[] args) throws Exception {
		/**
		 * 1.创建链接
		 * 2.创建channel
		 * 3.声明队列
		 * 4.声明交换机
		 * 5.队列与交换机进行绑定
		 * 6.监听消息
		 */
		
		Connection connection = MQConnection.connectionFactory();
		
		final Channel channel = connection.createChannel();
		
		channel.queueDeclare(MQParam.ACK_QUEUE_NAME, false, false, false, null);
		
		channel.exchangeDeclare(MQParam.ACK_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		
		channel.queueBind(MQParam.ACK_QUEUE_NAME,MQParam.ACK_EXCHANGE_NAME, MQParam.ACK_EXCHANGE_ROUTINGKEY);
		
		//设置每次只读取一条数据  保证其他消费者可以工作
		channel.basicQos(1);
		
		channel.basicConsume(MQParam.ACK_QUEUE_NAME, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				try {
					System.err.println("consumer Tag" + consumerTag);
					System.err.println("消息开始处理===============");
					System.err.println("获取到的消息为:" + new String(body,"UTF-8"));
					Thread.sleep(6000);
					System.err.println("消息完成处理===============");
					//重点======================================================
					channel.basicAck(envelope.getDeliveryTag(), false);
				} catch (Exception e) {
					//重点======================================================
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
				}
			}
		});
	}
}
```

## 消息的持久化

### 1.概述

如果RabbitMQ服务器停止,我们的交换机 队列 以及消息都会消息这个时候就需要持久化将数据从内存中保存到磁盘内

不持久的队列称为瞬态。并非所有场景和用例都要求队列持久。

队列的持久性不会使路由到该队列的消息持久。如果代理被删除然后重新启动，则在代理启动期间将重新声明持久队列，但是，只会 恢复持久性消息。

### 2.公共参数以及工具类

```java
package com.mq.utils;

public class PublicParam {
	/*Durable DirectInfo*/
	public static String DURABLE_DIRECT_EXCHANGE="direct_exchange_durable";
	public static String DURABLE_DIRECT_ROUTINGKEY = "direct_exchange_routingkey_durable";
	public static String DURABLE_QUEU_NAME="direct_queue_name_durable";
}
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
```



### 3.生产者

```java
package com.mq.durable;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.impl.AMQBasicProperties;

/**
 * 持久化 包括
 * 		队列持久化
 * 		创建队列   参数解析  1.队列名称  2.是否持久化   3.是否独占  4.是否字段删除 5.属性参数
 * 		channel.queueDeclare(PublicParam.DURABLE_QUEU_NAME, true, false, false, null);
 * 		消息持久化
 *		 发送消息 deliveryMode 取值 2 消息持久化  1 不持久化  
 * 		AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().deliveryMode(2).build();
		channel.basicPublish(PublicParam.DURABLE_DIRECT_EXCHANGE, PublicParam.DURABLE_DIRECT_ROUTINGKEY, basicProperties, "测试持久化 消息  队列 交换机".getBytes());
 * 		交换机持久化
 * 		创建交换机 参数解析 1.交换机名称 2.交换机类型 3.是否持久化
 * 		channel.exchangeDeclare(PublicParam.DURABLE_DIRECT_EXCHANGE, BuiltinExchangeType.DIRECT, true);
 * @author Administrator
 *
 */
public class DurableSend {
	public static void main(String[] args) throws Exception, Exception {
		/*创建链接*/
		Connection connection = ConnectionUtils.connection();
		/*创建管道*/
		Channel channel =  connection.createChannel();
		/*创建队列   参数解析  1.队列名称  2.是否持久化   3.是否独占  4.是否字段删除 5.属性参数*/
		channel.queueDeclare(PublicParam.DURABLE_QUEU_NAME, true, false, false, null);
		/*创建交换机 参数解析 1.交换机名称 2.交换机类型 3.是否持久化*/
		channel.exchangeDeclare(PublicParam.DURABLE_DIRECT_EXCHANGE, BuiltinExchangeType.DIRECT, true);
		/*队列与交换机进行绑定*/
		channel.queueBind(PublicParam.DURABLE_QUEU_NAME, PublicParam.DURABLE_DIRECT_EXCHANGE, PublicParam.DURABLE_DIRECT_ROUTINGKEY);
		/*发送消息 deliveryMode 取值 2 消息持久化  1 不持久化  */
		AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().deliveryMode(2).build();
		channel.basicPublish(PublicParam.DURABLE_DIRECT_EXCHANGE, PublicParam.DURABLE_DIRECT_ROUTINGKEY, basicProperties, "测试持久化 消息  队列 交换机".getBytes());
		
		System.gc();
		System.exit(1);
	}
}

```



### 4.消费者



```java
package com.mq.durable;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消息持久化消费者
 * @author Administrator
 *
 */
public class DurableRevc {
	public static void main(String[] args) throws IOException, Exception {
		/*创建链接*/
		Connection connection = ConnectionUtils.connection();
		/*创建通道*/
		final Channel channel = connection.createChannel();
		/*创建队列*/
		channel.queueDeclare(PublicParam.DURABLE_QUEU_NAME, true, false, false, null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.DURABLE_DIRECT_EXCHANGE, BuiltinExchangeType.DIRECT, true, false, null);
		/*交换机与队列进行绑定*/
		channel.queueBind(PublicParam.DURABLE_QUEU_NAME, PublicParam.DURABLE_DIRECT_EXCHANGE, PublicParam.DURABLE_DIRECT_ROUTINGKEY,null);
		/*创建消费者对象*/
		channel.basicConsume(PublicParam.DURABLE_QUEU_NAME, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				try{
					System.err.println("从【"+PublicParam.DURABLE_QUEU_NAME+"】中获取到的消息为: " + new String(body,"UTF-8"));                        
					//消息确认
					/*模拟消息处理*/
					Thread.sleep(3000);
					//如果在调用此方法之前关闭服务 那么消息按照未处理返回  消息将继续存在队列中
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					e.printStackTrace();
					/*参数分别为 1.消息标识  2.是否多条消息处理  3.是否重回队列*/
					channel.basicNack(envelope.getDeliveryTag(), false, true);
				}
			}
		});
	}
}
```



## RabbitMQ临时队列

```java
RaabitMq 临时队列
临时队列使用channel.queueDeclare() 进行声明可通过.getQueue()获取随机的队列名,临时队列在消费者存在时存在,消费者下线临时队列将消失

String queueName = channel.queueDeclare().getQueue();
```



## RabbitMQ发布订阅

### 1.概述

**什么是发布订阅**

**类似于微信订阅号,关注公众号后只要公众号发布消息订阅者都可获取到微信推送到的消息**

**实例代码**

**RabbitMq如何实现发布订阅**

**简单来讲就是将多个队列绑定同一个交换器(Exchange)然后多个消费者进行监听,生产者发布消息时绑定exchangeName 这样就实现了发布订阅模式**



运行流程

![](C:\Users\Administrator\Desktop\xi\rabbitmq\images\发布订阅示例图.png)

### 2.工具类

```java
package com.mq.utils;

public class PublicParam {
	/*Publicsh Subscribe Info*/
	public static String PUBSUB_FAOUNT_QUEUE_1="fount_publicsh_queue_1";
	public static String PUBSUB_FAOUNT_QUEUE_2="fount_publicsh_queue_2";
	public static String PUBSUB_FAOUNT_EXCHANGENAME_1="fount_publicsh_exchange_name";
	/*FOUNT 类型交换机不存在routingKey这一说所以滞空*/
	public static String PUBSUB_FAOUNT_ROUTINGKEY ="";
}
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
```

### 3.生产者

```java
package com.mq.publishsubscribe;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 *  生产者/发布
 *  
 *      一个生产者对应多个队列
 * @author Administrator
 *
 */
public class PublicshSend {
	public static void main(String[] args) throws Exception, Exception {
		/*创建链接*/
		Connection connection =  ConnectionUtils.connection();
		/*创建管道*/
		Channel channel = connection.createChannel();
		/*创建队列 1*/
		channel.queueDeclare(PublicParam.PUBSUB_FAOUNT_QUEUE_1,false,false,false,null);
		/*创建队列 2*/
		channel.queueDeclare(PublicParam.PUBSUB_FAOUNT_QUEUE_2,false,false,false,null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, BuiltinExchangeType.FANOUT, false, false, null);
		/*队列1 与交换机绑定*/
		channel.queueBind(PublicParam.PUBSUB_FAOUNT_QUEUE_1, PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, PublicParam.PUBSUB_FAOUNT_ROUTINGKEY);
		/*队列2 与交换机绑定*/
		channel.queueBind(PublicParam.PUBSUB_FAOUNT_QUEUE_2, PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, PublicParam.PUBSUB_FAOUNT_ROUTINGKEY);
		/*发布消息*/
		channel.basicPublish(PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, PublicParam.PUBSUB_FAOUNT_ROUTINGKEY, false, false, null, "测试发布订阅".getBytes());
	}
}
```

### 4.消费者



```java
package com.mq.publishsubscribe;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消费者1 
 * @author Administrator
 *
 */
public class SubscribeRecv {
	public static void main(String[] args) throws Exception, Exception {
		/*创建链接*/
		Connection connection =  ConnectionUtils.connection();
		/*创建管道*/
		final Channel channel = connection.createChannel();
		/*创建队列 1*/
		channel.queueDeclare(PublicParam.PUBSUB_FAOUNT_QUEUE_1,false,false,false,null);
		/*创建队列 2*/
		channel.queueDeclare(PublicParam.PUBSUB_FAOUNT_QUEUE_2,false,false,false,null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, BuiltinExchangeType.FANOUT, false, false, null);
		/*队列1 与交换机绑定*/
		channel.queueBind(PublicParam.PUBSUB_FAOUNT_QUEUE_1, PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, PublicParam.PUBSUB_FAOUNT_ROUTINGKEY);
		/*队列2 与交换机绑定*/
		channel.queueBind(PublicParam.PUBSUB_FAOUNT_QUEUE_2, PublicParam.PUBSUB_FAOUNT_EXCHANGENAME_1, PublicParam.PUBSUB_FAOUNT_ROUTINGKEY);
		/*消息监听1*/
		channel.basicConsume(PublicParam.PUBSUB_FAOUNT_QUEUE_1,false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				try{
					System.err.println("队列名称:["+PublicParam.PUBSUB_FAOUNT_QUEUE_1+"],获取到的消息为:" + new String(body,"UTF-8"));
					//模拟业务处理
					Thread.sleep(3000);
					//消息确认
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
					// TODO: handle exception
				}
			}
		});
		
		/*消息监听2*/
		channel.basicConsume(PublicParam.PUBSUB_FAOUNT_QUEUE_2,false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				try{
					System.err.println("队列名称:["+PublicParam.PUBSUB_FAOUNT_QUEUE_2+"],获取到的消息为:" + new String(body,"UTF-8"));
					//模拟业务处理
					Thread.sleep(3000);
					//消息确认
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
					// TODO: handle exception
				}
			}
		});
	}
}
```

## RabbitMQ消息属性的设置

### 常用API

```java
AMQP.BasicProperties.deliveryMode(2)    消息持久化 1 不持久化 2 持久化
AMQP.BasicProperties .contentEncoding("UTF8")                   //编码格式
AMQP.BasicProperties.expiration("30000")     //消息过期时间  单位毫秒
AMQP.BasicProperties.priority(2)           //优先级
AMQP.BasicProperties .headers(reqHead)  //请求头消息用于客户端获取
```

### 工具类



```java
package com.mq.utils;

public class PublicParam {
	public static String SIMPLE_QUEUENAME = "simple_queue";
	public static String WORK_QUEUENAME = "work_queue";
	
	/*Exchange  Info*/
	public static String FOUNT_QUEUE_NAME1="fount_quque_1";
	public static String FOUNT_QUEUE_NAME2="fount_quque_2";
	public static String FOUNT_EXCHANGE_NAME="fount_exchange";
	
	
	/*Topic Exchange*/
	public static String TOPIC_QUEUE_NAME1="topic_queue_1";
	public static String TOPIC_QUEUE_NAME2="topic_queue_2";
	public static String TOPIC_ROUTING_KEY="topic_routingKey";
	public static String TOPIC_EXCHANGE_NAME="topic_exchange_name";
	
	
	/*Direct Exchange*/
	public static String DIRECT_QUEUE_NAME="direct_queue_1";
	public static String DIRECT_ROUTING_KEY="direct_routing_key";
	public static String DIRECT_EXCHANGE_NAME="direct_exchange_name";
	
	
	/*ACK TopicExchange*/
	public static String ACK_TOPIC_EXCHANGE="topic_exchange_ack_1";
	public static String ACK_QUEUE_NAME="topic_queue_ack_1";
	public static String ACK_ROUTING_KEY="topic_routingkey_ack_1";
	
	
	/*Durable DirectInfo*/
	public static String DURABLE_DIRECT_EXCHANGE="direct_exchange_durable";
	public static String DURABLE_DIRECT_ROUTINGKEY = "direct_exchange_routingkey_durable";
	public static String DURABLE_QUEU_NAME="direct_queue_name_durable";
	
	
	/*Publicsh Subscribe Info*/
	public static String PUBSUB_FAOUNT_QUEUE_1="fount_publicsh_queue_1";
	public static String PUBSUB_FAOUNT_QUEUE_2="fount_publicsh_queue_2";
	public static String PUBSUB_FAOUNT_EXCHANGENAME_1="fount_publicsh_exchange_name";
	/*FOUNT 类型交换机不存在routingKey这一说所以滞空*/
	public static String PUBSUB_FAOUNT_ROUTINGKEY ="";
	
	/*Msg Properties Info*/
	public static String MSGPROPERTIES_DIRECT_QUEUE_1="msgproperties_direct_queue_1";
	public static String MSGPROPERTIES_DIRECT_EXCHANGE_NAME="msgproperties_direct_exchange";
	public static String MSGPROPERTIES_DIRECT_ROUTINGKEY="msgproperties_routingKey";
	
	
	/*Transaction Info*/
	public static String TRANSACTION_DIRECT_QUEUE="tranaction_direct_queue";
	public static String TRANSACTION_DIRECT_EXCHANGENAME="direct_exchange_transaction";
	public static String TRANSACTION_DIRECT_EXCHANGE_ROUTINGKEY = "direct_exchange_routingKey_transaction";
	
	
	/*Confirm Info*/
	public static String CONFIRM_TOPIC_QUEUE="confirm_topic_queue_name";
	public static String CONFIRM_TOPIC_EXCHANGE_NAME="confirm_topic_exchange_name";
	public static String CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY="confirm_topic_exchange_routingKey";
	
	/*ConfirmListener Info*/
	public static String CONFIRMLISTENER_DIRECT_QUEUE="confrimlistener_direct_queue_name";
	public static String CONFIRMLISTENER_DIRECT_EXCHANGE_NAME="confirmlistener_direct_exchange_name";
	public static String CONFIRMLISTENER_DIRECT_EXCHANGE_ROUTINGKEY = "confirmlistener_direct_exchange_name";
}

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
```



### 生产者



```java
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
```

### 消费者

```java
package com.mq.msgproperties;

import java.io.IOException;
import java.util.Map;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消费者
 * @author Administrator
 *
 */
public class MsgPropertiesRevc {
	public static void main(String[] args) throws IOException, Exception {
		/*创建连接*/
		Connection connection = ConnectionUtils.connection();
		/*创建通道*/
		final Channel channel = connection.createChannel();
		/*创建队列*/
		channel.queueDeclare(PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1, false, false, false, null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.MSGPROPERTIES_DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		/*交换机与队列进行绑定*/
		channel.queueBind(PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1, PublicParam.MSGPROPERTIES_DIRECT_EXCHANGE_NAME, PublicParam.MSGPROPERTIES_DIRECT_ROUTINGKEY);
		/*获取消息*/
		channel.basicConsume(PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				try{
					System.err.println("队列名称:["+PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1+"]获取到的消息为" + new String(body,"UTF-8"));
					Map<String, Object> heads = properties.getHeaders();
					System.err.println("队列名称:["+PublicParam.MSGPROPERTIES_DIRECT_QUEUE_1+"]获取到的额外信息为" + heads.toString());
					Thread.sleep(3000);//模拟业务处理
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
				}
			}
		});
	}
}
```



## RabbitMQ事务

### 手动开启,提交回滚事务

优点:

​	可以手动控制事务,**保证了消息安全性发送到MQ的**

缺点:

​	发送一条消息需进行三次网络连接效率不好

**实现方式**

**通过channel.tx\*实现 具体实现方法为**

**channel.txSelect();**    **开启事物**   

**Channel.txcommit();  提交事物**

**Channel.txrollback();  回滚事物**



#### 示例代码:

##### 	工具类

```java
package com.mq.utils;

public class PublicParam {
	public static String SIMPLE_QUEUENAME = "simple_queue";
	public static String WORK_QUEUENAME = "work_queue";
	
	/*Exchange  Info*/
	public static String FOUNT_QUEUE_NAME1="fount_quque_1";
	public static String FOUNT_QUEUE_NAME2="fount_quque_2";
	public static String FOUNT_EXCHANGE_NAME="fount_exchange";
	
	
	/*Topic Exchange*/
	public static String TOPIC_QUEUE_NAME1="topic_queue_1";
	public static String TOPIC_QUEUE_NAME2="topic_queue_2";
	public static String TOPIC_ROUTING_KEY="topic_routingKey";
	public static String TOPIC_EXCHANGE_NAME="topic_exchange_name";
	
	
	/*Direct Exchange*/
	public static String DIRECT_QUEUE_NAME="direct_queue_1";
	public static String DIRECT_ROUTING_KEY="direct_routing_key";
	public static String DIRECT_EXCHANGE_NAME="direct_exchange_name";
	
	
	/*ACK TopicExchange*/
	public static String ACK_TOPIC_EXCHANGE="topic_exchange_ack_1";
	public static String ACK_QUEUE_NAME="topic_queue_ack_1";
	public static String ACK_ROUTING_KEY="topic_routingkey_ack_1";
	
	
	/*Durable DirectInfo*/
	public static String DURABLE_DIRECT_EXCHANGE="direct_exchange_durable";
	public static String DURABLE_DIRECT_ROUTINGKEY = "direct_exchange_routingkey_durable";
	public static String DURABLE_QUEU_NAME="direct_queue_name_durable";
	
	
	/*Publicsh Subscribe Info*/
	public static String PUBSUB_FAOUNT_QUEUE_1="fount_publicsh_queue_1";
	public static String PUBSUB_FAOUNT_QUEUE_2="fount_publicsh_queue_2";
	public static String PUBSUB_FAOUNT_EXCHANGENAME_1="fount_publicsh_exchange_name";
	/*FOUNT 类型交换机不存在routingKey这一说所以滞空*/
	public static String PUBSUB_FAOUNT_ROUTINGKEY ="";
	
	/*Msg Properties Info*/
	public static String MSGPROPERTIES_DIRECT_QUEUE_1="msgproperties_direct_queue_1";
	public static String MSGPROPERTIES_DIRECT_EXCHANGE_NAME="msgproperties_direct_exchange";
	public static String MSGPROPERTIES_DIRECT_ROUTINGKEY="msgproperties_routingKey";
	
	
	/*Transaction Info*/
	public static String TRANSACTION_DIRECT_QUEUE="tranaction_direct_queue";
	public static String TRANSACTION_DIRECT_EXCHANGENAME="direct_exchange_transaction";
	public static String TRANSACTION_DIRECT_EXCHANGE_ROUTINGKEY = "direct_exchange_routingKey_transaction";
	
	
	/*Confirm Info*/
	public static String CONFIRM_TOPIC_QUEUE="confirm_topic_queue_name";
	public static String CONFIRM_TOPIC_EXCHANGE_NAME="confirm_topic_exchange_name";
	public static String CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY="confirm_topic_exchange_routingKey";
	
	/*ConfirmListener Info*/
	public static String CONFIRMLISTENER_DIRECT_QUEUE="confrimlistener_direct_queue_name";
	public static String CONFIRMLISTENER_DIRECT_EXCHANGE_NAME="confirmlistener_direct_exchange_name";
	public static String CONFIRMLISTENER_DIRECT_EXCHANGE_ROUTINGKEY = "confirmlistener_direct_exchange_name";
}
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
```



##### 生产者

```java
package com.mq.transaction.tx;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 测试 基于事物的消息发送 
 * 	tx select 开启事物
 * @author Administrator
 *
 */
public class TransactionSend {
	public static void main(String[] args) throws IOException, Exception {
		/*创建链接*/
		Connection connection =  ConnectionUtils.connection();
		/*创建管道*/
		Channel channel = connection.createChannel();
		/*创建队列*/
		channel.queueDeclare(PublicParam.TRANSACTION_DIRECT_QUEUE, false, false, false, null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.TRANSACTION_DIRECT_EXCHANGENAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		/*交换机与队列进行Binding*/
		channel.queueBind(PublicParam.TRANSACTION_DIRECT_QUEUE, PublicParam.TRANSACTION_DIRECT_EXCHANGENAME, PublicParam.TRANSACTION_DIRECT_EXCHANGE_ROUTINGKEY, null);
		/*开启事物*/
		channel.txSelect();
		/*发送消息*/
		channel.basicPublish(PublicParam.TRANSACTION_DIRECT_EXCHANGENAME,  PublicParam.TRANSACTION_DIRECT_EXCHANGE_ROUTINGKEY, false, false, null, "测试事务发送消息".getBytes());
		/*提交事物/回滚事物(事务回滚后消息将不会发送到MQ中)*/
//		channel.txCommit();
		channel.txRollback();
		System.err.println("Msg  send  Success");
		System.exit(1);
	}
}
```

##### 消费者

```java
package com.mq.transaction.tx;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 基于事务的消费者 
 * 
 * 			没什么变化
 * @author Administrator
 *
 */
public class TransactionRevc {
	public static void main(String[] args) throws IOException, Exception {
		/*创建链接*/
		Connection connection =  ConnectionUtils.connection();
		/*创建管道*/
		final Channel channel = connection.createChannel();
		/*创建队列*/
		channel.queueDeclare(PublicParam.TRANSACTION_DIRECT_QUEUE, false, false, false, null);
		/*创建交换机*/
		channel.exchangeDeclare(PublicParam.TRANSACTION_DIRECT_EXCHANGENAME, BuiltinExchangeType.DIRECT, false, false, false, null);
		/*交换机与队列进行Binding*/
		channel.queueBind(PublicParam.TRANSACTION_DIRECT_QUEUE, PublicParam.TRANSACTION_DIRECT_EXCHANGENAME, PublicParam.TRANSACTION_DIRECT_EXCHANGE_ROUTINGKEY, null);
		/*每次只读取一条记录*/
		channel.basicQos(1);
		/*监听*/
		channel.basicConsume(PublicParam.TRANSACTION_DIRECT_QUEUE, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				// TODO Auto-generated method stub
				try{
					System.err.println("从队列:["+PublicParam.TRANSACTION_DIRECT_QUEUE+"],获取到的消息结果为：" + new String(body,"UTF-8"));
					//模拟消息处理
					Thread.sleep(3000);
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
				}
			}
		});
	}
}
```

### Confirm模式进行消息确认

Confirm发送方确认模式使用和事务类似，也是通过设置Channel进行发送方确认的。



**Confirm的三种实现方式：**

方式一：channel.waitForConfirms()普通发送方确认模式；

方式二：channel.waitForConfirmsOrDie()批量确认模式；

方式三：channel.addConfirmListener()异步监听发送方确认模式；

####  示例代码公共

```java
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
package com.mq.utils;

public class PublicParam {
	public static String SIMPLE_QUEUENAME = "simple_queue";
	public static String WORK_QUEUENAME = "work_queue";
	
	/*Exchange  Info*/
	public static String FOUNT_QUEUE_NAME1="fount_quque_1";
	public static String FOUNT_QUEUE_NAME2="fount_quque_2";
	public static String FOUNT_EXCHANGE_NAME="fount_exchange";
	
	
	/*Topic Exchange*/
	public static String TOPIC_QUEUE_NAME1="topic_queue_1";
	public static String TOPIC_QUEUE_NAME2="topic_queue_2";
	public static String TOPIC_ROUTING_KEY="topic_routingKey";
	public static String TOPIC_EXCHANGE_NAME="topic_exchange_name";
	
	
	/*Direct Exchange*/
	public static String DIRECT_QUEUE_NAME="direct_queue_1";
	public static String DIRECT_ROUTING_KEY="direct_routing_key";
	public static String DIRECT_EXCHANGE_NAME="direct_exchange_name";
	
	
	/*ACK TopicExchange*/
	public static String ACK_TOPIC_EXCHANGE="topic_exchange_ack_1";
	public static String ACK_QUEUE_NAME="topic_queue_ack_1";
	public static String ACK_ROUTING_KEY="topic_routingkey_ack_1";
	
	
	/*Durable DirectInfo*/
	public static String DURABLE_DIRECT_EXCHANGE="direct_exchange_durable";
	public static String DURABLE_DIRECT_ROUTINGKEY = "direct_exchange_routingkey_durable";
	public static String DURABLE_QUEU_NAME="direct_queue_name_durable";
	
	
	/*Publicsh Subscribe Info*/
	public static String PUBSUB_FAOUNT_QUEUE_1="fount_publicsh_queue_1";
	public static String PUBSUB_FAOUNT_QUEUE_2="fount_publicsh_queue_2";
	public static String PUBSUB_FAOUNT_EXCHANGENAME_1="fount_publicsh_exchange_name";
	/*FOUNT 类型交换机不存在routingKey这一说所以滞空*/
	public static String PUBSUB_FAOUNT_ROUTINGKEY ="";
	
	/*Msg Properties Info*/
	public static String MSGPROPERTIES_DIRECT_QUEUE_1="msgproperties_direct_queue_1";
	public static String MSGPROPERTIES_DIRECT_EXCHANGE_NAME="msgproperties_direct_exchange";
	public static String MSGPROPERTIES_DIRECT_ROUTINGKEY="msgproperties_routingKey";
	
	
	/*Transaction Info*/
	public static String TRANSACTION_DIRECT_QUEUE="tranaction_direct_queue";
	public static String TRANSACTION_DIRECT_EXCHANGENAME="direct_exchange_transaction";
	public static String TRANSACTION_DIRECT_EXCHANGE_ROUTINGKEY = "direct_exchange_routingKey_transaction";
	
	
	/*Confirm Info*/
	public static String CONFIRM_TOPIC_QUEUE="confirm_topic_queue_name";
	public static String CONFIRM_TOPIC_EXCHANGE_NAME="confirm_topic_exchange_name";
	public static String CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY="confirm_topic_exchange_routingKey";
	
	/*ConfirmListener Info*/
	public static String CONFIRMLISTENER_DIRECT_QUEUE="confrimlistener_direct_queue_name";
	public static String CONFIRMLISTENER_DIRECT_EXCHANGE_NAME="confirmlistener_direct_exchange_name";
	public static String CONFIRMLISTENER_DIRECT_EXCHANGE_ROUTINGKEY = "confirmlistener_direct_exchange_name";
}

```



#### 方式1 方式2示例代码

```java
package com.mq.transaction.confirm;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 通过Confirm方式进行发送消息确认
 * @author Administrator
 *
 */
public class ConfirmSend {
	public static void main(String[] args) throws IOException, Exception {
		//普通Confirm模式
		simpleConfirm();
		//批量confirm模式
		msgConfirms();
	}
	//批量的消息确认
	public static void msgConfirms() throws Exception{
		Connection connection = ConnectionUtils.connection();
		
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(PublicParam.CONFIRM_TOPIC_QUEUE, false, false, false, null);
		
		channel.exchangeDeclare(PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, false, false, null);
		
		channel.queueBind(PublicParam.CONFIRM_TOPIC_QUEUE, PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME, PublicParam.CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY + ".#");
		/*开启事物*/
		channel.confirmSelect();
		for (int i = 0; i < 10; i++) {
			channel.basicPublish(PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME,  PublicParam.CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY+ ".1", false, null, "测试Confirm模式下发送消息".getBytes());
		}
		/*判断消息是否成功发送 如果失败抛出Io异常*/
		channel.waitForConfirmsOrDie();
		System.exit(1);
	}
	//普通的Confirm消息确认
	public static void simpleConfirm() throws Exception, Exception{
		Connection connection = ConnectionUtils.connection();
		
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(PublicParam.CONFIRM_TOPIC_QUEUE, false, false, false, null);
		
		channel.exchangeDeclare(PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, false, false, null);
		
		channel.queueBind(PublicParam.CONFIRM_TOPIC_QUEUE, PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME, PublicParam.CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY + ".#");
		/*开启事物*/
		channel.confirmSelect();
		channel.basicPublish(PublicParam.CONFIRM_TOPIC_EXCHANGE_NAME,  PublicParam.CONFIRM_TOPIC_EXCHANGE_ROUTINGKEY+ ".1", false, null, "测试Confirm模式下发送消息".getBytes());

		/*判断消息是否成功发送*/
		String restStr = channel.waitForConfirms()?"成功":"失败";
		System.err.println("消息发送结果为： " + restStr);
		System.exit(1);
	}
}
```



#### Confirm异步监听发送方确认模式

##### 生产者



```java
package com.mq.transaction.confirmlistener;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 基于Confirm Listener 完成消息确认
 * 
 * @author Administrator
 * 
 */
public class ConfirmListenerSend {
	public static void main(String[] args) throws Exception {
		Connection con = ConnectionUtils.connection();
		
		Channel channel = con.createChannel();
		
		channel.queueDeclare(PublicParam.CONFIRMLISTENER_DIRECT_QUEUE, true, false, false, null);
		
		channel.exchangeDeclare(PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, false, null);
		
		channel.queueBind(PublicParam.CONFIRMLISTENER_DIRECT_QUEUE, PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_NAME, PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_ROUTINGKEY);
		/*开启发送确认*/
		channel.confirmSelect();
		/* mandatory 一定将此设置为true 这样才能使returnlistener生效*/
		channel.basicPublish(PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_NAME,PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_ROUTINGKEY, false,false,null, "基于异步消息确认所发送的消息".getBytes());
		/*在未匹配到队列中调用*/
		channel.addConfirmListener(new ConfirmListener() {
			
			public void handleNack(long deliveryTag, boolean multiple)
					throws IOException {
				// TODO Auto-generated method stub
				System.err.println("消息未被确认" + deliveryTag);
			}
			
			public void handleAck(long deliveryTag, boolean multiple)
					throws IOException {
				// TODO Auto-generated method stub
				System.err.println("消息已经被确认" + deliveryTag);
			}
		});
	}
}
```

##### 消费者实现消息确认  channel.ack    channel.nack

```java
package com.mq.transaction.confirmlistener;

import java.io.IOException;

import com.mq.utils.ConnectionUtils;
import com.mq.utils.PublicParam;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * 消费者
 * 
 * @author Administrator
 * 
 */
public class ConfirmListenerRecv {
	public static void main(String[] args) throws IOException, Exception {
Connection con = ConnectionUtils.connection();
		
		final Channel channel = con.createChannel();
		
		channel.queueDeclare(PublicParam.CONFIRMLISTENER_DIRECT_QUEUE, true, false, false, null);
		
		channel.exchangeDeclare(PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, false, null);
		
		channel.queueBind(PublicParam.CONFIRMLISTENER_DIRECT_QUEUE, PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_NAME, PublicParam.CONFIRMLISTENER_DIRECT_EXCHANGE_ROUTINGKEY);
		
		channel.basicConsume(PublicParam.CONFIRMLISTENER_DIRECT_QUEUE, false, new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				try{
					System.err.println("队列名:[" + PublicParam.CONFIRMLISTENER_DIRECT_QUEUE + "],获取到的消息为:" + new String(body,"UTF-8"));
					Thread.sleep(3000);
					channel.basicAck(envelope.getDeliveryTag(), false);
				}catch (Exception e) {
					// TODO: handle exception
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					e.printStackTrace();
				}
			}
		});
	}
}
```

## RabbitMQ消息未路由返回

Return Listener用于处理一些不可路由的消息

例如:消息生成这通过指定一个Exchange和RoutingKey 把消息送达到某一个队列中去,然后消费者进行监听进行消费处理,但是在某些情况下,如果我们在发送消息的时候,当前的exchange不存在或指定路由Key路由不到,这个时候如果我们需要监听这种不可达的消息,就要使用Return Listener！

```java
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
```

## 消费端限流

```java
Qos服务质量的保障,在非自动确认消息的前提下,如果Consumer端持有的消息未进行手动确认的话那么MQ将不在将消息发送该Consumer端
设置Qos
//参数1 消息大小  参数2 消息条数  参数 3 是否只对当前Channel 生效 true Channel级别  falseConsumer级别
channel.basicQos(0,1,false);
```

## TTL 消息有效期

```java
设置消息的有效期
AMQP.BasicProperties basicProperties = new 
//设置有效期为60秒  单位毫秒
AMQP.BasicProperties().builder().expiration("60000").build();
channel.basicPublish("","layz_prife_name",true,false,basicProperties,msg.getBytes());
```

## 消息的延迟发送

消息发送后到消息队列中设置消息的有效期以及死信队列,当消息过期后MQ主动发起请求通知配置好的交换机进行消息的发送

实现步骤:

\1. 创建交换器以及Queue 和Consumer 进行监听

\2. 创建Produce创建一个交换器以及queue这个新建的交换器不被监听

\3. Produce发送消息时设置消息过期时间,以及消息过期后进入的exchange和routingKey

\4. 开启Consumer 和 Producer

示例图:

​	![](C:\Users\Administrator\Desktop\xi\rabbitmq\images\消息的延迟发送png.png)

生产者示例代码

```java
package com.mq.msgdelay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.mq.utils.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 延迟发送消息
 * @author Administrator
 *
 */
public class DelaySendMsg {
	public static final String DELAY_1_QUEUE_NAME="delay_1_queue_name";
	public static final String DELAY_1_EXCHANGE_NAME="delay_1_exchange_name";
	public static final String DLAY_1_EXCHANGE_TYPE=BuiltinExchangeType.DIRECT.getType();
	public static final String DLAY_1_EXCHANGE_ROUTINGKEY="delay_1_exchange_routingkey";
	
	public static final String DELAY_2_QUEUE_NAME="delay_2_queue_name";
	public static final String DELAY_2_EXCHANGE_NAME="delay_2_exchange_name";
	public static final String DLAY_2_EXCHANGE_TYPE=BuiltinExchangeType.DIRECT.getType();
	public static final String DLAY_2_EXCHANGE_ROUTINGKEY="delay_2_exchange_routingkey";
	
	public static void main(String[] args) throws Exception, Exception {
		Connection connection = ConnectionUtils.connection();
		
		Channel channel = connection.createChannel();

		//设置队列信息超时后转接的队列
		Map<String,Object> headers = new HashMap<String, Object>();
		headers.put("x-dead-letter-exchange",DELAY_2_EXCHANGE_NAME);
		headers.put("x-dead-letter-routing-key",DLAY_2_EXCHANGE_ROUTINGKEY);
		//申明第一个队列信息
		channel.queueDeclare(DELAY_1_QUEUE_NAME, false, false, false, headers);
		
		channel.exchangeDeclare(DELAY_1_EXCHANGE_NAME, DLAY_1_EXCHANGE_TYPE, false);
		
		channel.queueBind(DELAY_1_QUEUE_NAME, DELAY_1_EXCHANGE_NAME, DLAY_1_EXCHANGE_ROUTINGKEY);
		
		//申明第二个队列信息
		channel.queueDeclare(DELAY_2_QUEUE_NAME, false, false, false, null);
		
		channel.exchangeDeclare(DELAY_2_EXCHANGE_NAME, DLAY_2_EXCHANGE_TYPE, false);
		
		channel.queueBind(DELAY_2_QUEUE_NAME, DELAY_2_EXCHANGE_NAME, DLAY_2_EXCHANGE_ROUTINGKEY);
		
//		headers.put("x-message-ttl", "300");
//		BasicProperties basicProperties = new BasicProperties().builder().headers(headers).expiration("6000").build();
		 AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().expiration("3000").build();
		channel.basicPublish(DELAY_1_EXCHANGE_NAME, DLAY_1_EXCHANGE_ROUTINGKEY, basicProperties, "发送延迟消息咯".getBytes());
	}
}
```

## RabbitMQ集成SpringBoot

pom

```
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
    </dependencies>
```



### 生产者

#### 

#### 2.配置类

```java
package com.cn.mq.send.config;

import java.io.UnsupportedEncodingException;
import java.util.Formatter.BigDecimalLayoutForm;
import java.util.Map;

import javax.security.auth.callback.ConfirmationCallback;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cn.mq.send.util.RabbitPublicParam;

/**
 * Rabbitmq配置类
 * 
 * @author Administrator
 * 
 */
@Configuration
public class RabbitMqConfiguration {
	@Autowired/*这里不做自定义的声明因为在application.properties文件中已经声明了对应的属性并且RabbitAutoConfiguration已经进行了CachingConnectionFactory对象的声明*/
	private CachingConnectionFactory cachingConnectionFactory;

	// @Bean
	// public RabbitAdmin rabbitAdmin(){
	// RabbitAdmin rabbitAdmin = new RabbitAdmin(cachingConnectionFactory);
	// rabbitAdmin.setAutoStartup(true);
	// System.err.println("初始化Rabbit Admin");
	// return rabbitAdmin;
	// }
	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(
				cachingConnectionFactory);
		System.err.println("初始化RabbitMQTemplate");
		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
			@Override
			public void returnedMessage(Message message, int replyCode,
					String replyText, String exchange, String routingKey) {
				// TODO Auto-generated method stub
				System.err
						.println("======================未成功路由的消息信息为=================");
				System.err.println("message Info" + message.toString());
				try {
					System.err.println("msg "
							+ new String(message.getBody(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.err.println("replyCode Info" + replyCode);
				System.err.println("replyText Info" + replyText);
				System.err.println("exchange Info" + exchange);
				System.err.println("routingKey Info" + routingKey);
			}
		});
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
			@Override
			public void confirm(CorrelationData correlationData, boolean ack,
					String cause) {
				System.err
						.println("======================消息确认=================");
				System.err.println("correlationData Info" + correlationData);
				System.err.println("ack Info" + ack);
				System.err.println("cause Info" + cause);
			}
		});
		return rabbitTemplate;
	}
	/*----------------------------------------------fount exchange-----------------------------------------------*/
	/* 声明队列 */
	@Bean
	public Queue fountQueue(){
		/*String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments*/
		 return new Queue(RabbitPublicParam.SPRFOUNTQUEUE,false,false,false,null);
	}
	/*声明交换机*/
	@Bean
	public FanoutExchange fanoutExchange(){
		return new FanoutExchange(RabbitPublicParam.SPRFOUNTEXCHANGENAME, false, false, null);
	}
	/*交换机与队列进行绑定*/
	@Bean
	public Binding fanoutExchangeAndQueueBinding(){
		/*String destination, DestinationType destinationType, String exchange, String routingKey,
			Map<String, Object> arguments*/
		return new Binding(RabbitPublicParam.SPRFOUNTQUEUE,Binding.DestinationType.QUEUE,RabbitPublicParam.SPRFOUNTEXCHANGENAME,RabbitPublicParam.SPRFOUNTEXCHANGEROUTINGKEY,null);
	}
	
	/*----------------------------------------------topic exchange-----------------------------------------------*/
	@Bean
	public Queue topictQueue(){
		return new Queue(RabbitPublicParam.SPRTOPICQUEUE, false, false, false,null);
	}
	@Bean
	public TopicExchange topicExchange(){
		return new TopicExchange(RabbitPublicParam.SPRTOPICEXCHANGENAME, false, false, null);
	}
	@Bean
	public Binding topicBinding(){
		return new Binding(RabbitPublicParam.SPRTOPICQUEUE,DestinationType.QUEUE,RabbitPublicParam.SPRTOPICEXCHANGENAME,RabbitPublicParam.SPRTOPICEXCHANGEROUTINGKEY + ".#",null);
	}
	
	/*----------------------------------------------Direct exchange-----------------------------------------------*/
	@Bean
	public Queue directQueue(){
		System.err.println("初始化 direct Queue   :" +RabbitPublicParam.SprDirectQueue) ;
		return new Queue(RabbitPublicParam.SprDirectQueue, false, false, false,null);
	}
	@Bean
	public DirectExchange directExchange(){
		System.err.println("初始化 direct exchange    :" + RabbitPublicParam.SprDirectExchangeName);
		return new DirectExchange(RabbitPublicParam.SprDirectExchangeName, false, false, null);
	}
	@Bean
	public Binding directBinding(){
		System.err.println("初始化 binding  ququeName : " + RabbitPublicParam.SprDirectQueue + "Exchange Name :" + RabbitPublicParam.SprDirectExchangeName +  " routingKey   :" + RabbitPublicParam.SprDirectExchangeRoutingKey);
		return new Binding(RabbitPublicParam.SprDirectQueue,DestinationType.QUEUE,RabbitPublicParam.SprDirectExchangeName,RabbitPublicParam.SprDirectExchangeRoutingKey,null);
	}
}
```

#### 3.公共参数

```java
package com.cn.mq.send.util;

public class RabbitPublicParam {
	/*Spr fount*/
	public static final String SPRFOUNTQUEUE="spr_fount_queue";
	public static final String SPRFOUNTEXCHANGENAME="spr_fount_exchange_name";
	public static final String SPRFOUNTEXCHANGEROUTINGKEY="spr_fount_exchange_routingkey";
	
	
	/*Spr topic*/
	public static final String SPRTOPICQUEUE="spr_topic_queue";
	public static final String SPRTOPICEXCHANGENAME="spr_topic_exchange_name";
	public static final String SPRTOPICEXCHANGEROUTINGKEY="spr_topic_exchange_routingkey";
	
	/*Spr direct*/
	public static final String SprDirectQueue="spr_direct_queue";
	public static final String SprDirectExchangeName="spr_direct_exchange_name";
	public static final String SprDirectExchangeRoutingKey="spr_direct_exchange_routingkey";
}

```

#### 4.测试类

```java
package com.cn.mq.send;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cn.mq.send.util.RabbitPublicParam;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RabbitMQApp.class)
public class TestRabbiMQ {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	public void fountSendMsg(){
		//MessagePostProcessor messagePostProcessor, CorrelationData correlationData
//		MessagePostProcessor messagePostProcessor = new 
		try{
			//测试 发送Fount类型的消息
//			rabbitTemplate.convertAndSend(RabbitPublicParam.SPRFOUNTEXCHANGENAME, RabbitPublicParam.SPRFOUNTEXCHANGEROUTINGKEY, "测试消息");
			MessageProperties properties = new MessageProperties();
			//设置发送的消息为持久化
			properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
			//放入请求头
			properties.setHeader("name", "未");
			//放入请求头 
			properties.setHeader("wei", "wang");
			//过期时间
			properties.setExpiration("10000");
			//消息对象
			Message msg = new Message("测试发送Fount类型消息".getBytes(),properties);
			
			CorrelationData correlationData = new CorrelationData("123321");
			rabbitTemplate.send(RabbitPublicParam.SPRFOUNTEXCHANGENAME, RabbitPublicParam.SPRFOUNTEXCHANGEROUTINGKEY, msg, correlationData);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	@Test
	public void topicSendMsg(){
		try{
			//final String exchange, final String routingKey,final Message message, final CorrelationData correlationData
			//测试 发送Fount类型的消息
			MessageProperties properties = new MessageProperties();
			//设置发送的消息为持久化
			properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
			//放入请求头
			properties.setHeader("name", "topic method");
			//放入请求头 
			properties.setHeader("wei", "wang");
			//过期时间  如果消息未在有效期内被消费则消息被销毁
//			properties.setExpiration("10000");
			//消息对象
			CorrelationData correlationData = new CorrelationData("123321");
			for (int i = 0; i < 5; i++) {
				String message= "测试发送Topic类型消息"+i + "";
				Message msg = new Message(message.getBytes(),properties);
				rabbitTemplate.send(RabbitPublicParam.SPRTOPICEXCHANGENAME,RabbitPublicParam.SPRTOPICEXCHANGEROUTINGKEY+".1",msg,correlationData);
			}
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	@Test
	public void directSendMsg(){
		//测试 发送Fount类型的消息
		MessageProperties properties = new MessageProperties();
		//设置发送的消息为持久化
		properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		//放入请求头
		properties.setHeader("name", "direct method");
		//放入请求头 
		properties.setHeader("wei", "wang");
		//过期时间  如果消息未在有效期内被消费则消息被销毁
		properties.setExpiration("10000");
		//消息对象
		Message msg = new Message("测试发送Fount类型消息".getBytes(),properties);
		CorrelationData correlationData = new CorrelationData("6765334567");
		rabbitTemplate.send(RabbitPublicParam.SprDirectExchangeName, RabbitPublicParam.SprDirectExchangeRoutingKey, msg, correlationData);
		while (true) {}
	}
}

```

#### 5.核心配置类

```properties

#Rabbit Config 
# IP RabbitIp   defaultValue locahost
spring.rabbitmq.host=127.0.0.1
#port RabbitPort defaultValue 5672
spring.rabbitmq.port=5672
#登录帐号密码
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
# vhost
spring.rabbitmq.virtualHost=/
#是否开启消息确认
spring.rabbitmq.publisherConfirms=true
#如果消息未发布到队列中触发returns
spring.rabbitmq.publisherReturns=true
#链接超时时间
spring.rabbitmq.connectionTimeout=3000
```

#### 6.发送延迟消息

```
rabbitTemplate.convertAndSend(RabbitPublicParam.SPRFOUNTEXCHANGENAME, RabbitPublicParam.SPRFOUNTEXCHANGEROUTINGKEY, "测试延迟队列", new MessagePostProcessor() {
				
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					// TODO Auto-generated method stub
					//重点
					message.getMessageProperties().setHeader("x-delay",5000);
					System.err.println("消息信息为:" + message.getMessageProperties().getHeaders().get("x-delay"));
					return message;
				}
			});
```



### 消费者

#### 1.配置类

```java
package com.cn.mq.revc.config;

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
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cn.mq.revc.util.RabbitPublicParam;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

@Configuration
public class RabbitMqConfiguration {
	@Bean
	public SimpleMessageListenerContainer simpleMessageListenerContainer(
			CachingConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
		// 注入链接工厂
		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		// 设置监听的队列 可传递多个
		simpleMessageListenerContainer.setQueueNames("spr_topic_queue");
		// 设置消费者数量
		simpleMessageListenerContainer.setConcurrentConsumers(1);
		// 设置最大消费者数量
		simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
		// 消息是否重回队列
		simpleMessageListenerContainer.setDefaultRequeueRejected(true);
		// 类似 channel qos 设置消息数量
		simpleMessageListenerContainer.setPrefetchCount(1);
//		simpleMessageListenerContainer.setMessageConverter(messageConverter)
		// Auto 自动
		// simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
		// 消息自动确认的监听设置监听
		// simpleMessageListenerContainer.setMessageListener(new
		// MessageListener() {
		// @Override
		// public void onMessage(Message message) {
		// System.err.println("客户端获取到的消息为:" + new String(message.getBody()));
		// System.err.println("ConsumerTag" +
		// message.getMessageProperties().getConsumerTag());
		// }
		// });
		// MANUAL 手动
		simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		//监听  这里直接写实现  正式代码 需要编写实体类进行继承接口实现方法
		simpleMessageListenerContainer
				.setMessageListener(new ChannelAwareMessageListener() {
					@Override
					public void onMessage(Message message, Channel channel)
							throws Exception {
						try {
							System.out.println("channel + :" + channel.toString() + "channel.getChannelNumber() + :" + channel.getChannelNumber());
							System.err.println("获取到的消息为："+ new String(message.getBody(), "UTF-8"));
							Thread.sleep(10000);//模拟业务处理
							//消息确认
							channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
							System.err.println("业务处理完成 进行消息确认！");
							System.out.println("channel" + channel.toString() + "channel.getChannelNumber()" + channel.getChannelNumber());
						} catch (Exception e) {
							channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
							e.printStackTrace();
						}
					}
				});
		// 消费者标签无用
//		 simpleMessageListenerContainer.setConsumerTagStrategy(new
//			 ConsumerTagStrategy() {
//			 @Override
//			 public String createConsumerTag(String s) {
//			 return s + "☆" + UUID.randomUUID().toString();
//			 }
//		 });
		return simpleMessageListenerContainer;
	}

	/*----------------------------------------------fount exchange-----------------------------------------------*/
	/* 声明队列 */
	@Bean
	public Queue fountQueue() {
		System.err.println("初始化队列");
		/*
		 * String name, boolean durable, boolean exclusive, boolean autoDelete,
		 * Map<String, Object> arguments
		 */
		return new Queue(RabbitPublicParam.SPRFOUNTQUEUE, false, false, false,
				null);
	}

	/* 声明交换机 */
	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(RabbitPublicParam.SPRFOUNTEXCHANGENAME,
				false, false, null);
	}

	/* 交换机与队列进行绑定 */
	@Bean
	public Binding fanoutExchangeAndQueueBinding() {
		/*
		 * String destination, DestinationType destinationType, String exchange,
		 * String routingKey, Map<String, Object> arguments
		 */
		return new Binding(RabbitPublicParam.SPRFOUNTQUEUE,
				Binding.DestinationType.QUEUE,
				RabbitPublicParam.SPRFOUNTEXCHANGENAME,
				RabbitPublicParam.SPRFOUNTEXCHANGEROUTINGKEY, null);
	}

	/*----------------------------------------------topic exchange-----------------------------------------------*/
	@Bean
	public Queue topictQueue() {
		return new Queue(RabbitPublicParam.SPRTOPICQUEUE, false, false, false,
				null);
	}

	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange(RabbitPublicParam.SPRTOPICEXCHANGENAME, false,
				false, null);
	}

	@Bean
	public Binding topicBinding() {
		return new Binding(RabbitPublicParam.SPRTOPICQUEUE,
				DestinationType.QUEUE, RabbitPublicParam.SPRTOPICEXCHANGENAME,
				RabbitPublicParam.SPRTOPICEXCHANGEROUTINGKEY + ".#", null);
	}

	/*----------------------------------------------Direct exchange-----------------------------------------------*/
	@Bean
	public Queue directQueue() {
		System.err.println("初始化 direct Queue   :"
				+ RabbitPublicParam.SprDirectQueue);
		return new Queue(RabbitPublicParam.SprDirectQueue, false, false, false,
				null);
	}

	@Bean
	public DirectExchange directExchange() {
		System.err.println("初始化 direct exchange    :"
				+ RabbitPublicParam.SprDirectExchangeName);
		return new DirectExchange(RabbitPublicParam.SprDirectExchangeName,
				false, false, null);
	}

	@Bean
	public Binding directBinding() {
		System.err.println("初始化 binding  ququeName : "
				+ RabbitPublicParam.SprDirectQueue + "Exchange Name :"
				+ RabbitPublicParam.SprDirectExchangeName + " routingKey   :"
				+ RabbitPublicParam.SprDirectExchangeRoutingKey);
		return new Binding(RabbitPublicParam.SprDirectQueue,
				DestinationType.QUEUE, RabbitPublicParam.SprDirectExchangeName,
				RabbitPublicParam.SprDirectExchangeRoutingKey, null);
	}
}
```

#### 2.公共参数

```java
package com.cn.mq.revc.util;

public class RabbitPublicParam {
	/*Spr fount*/
	public static final String SPRFOUNTQUEUE="spr_fount_queue";
	public static final String SPRFOUNTEXCHANGENAME="spr_fount_exchange_name";
	public static final String SPRFOUNTEXCHANGEROUTINGKEY="spr_fount_exchange_routingkey";
	
	
	/*Spr topic*/
	public static final String SPRTOPICQUEUE="spr_topic_queue";
	public static final String SPRTOPICEXCHANGENAME="spr_topic_exchange_name";
	public static final String SPRTOPICEXCHANGEROUTINGKEY="spr_topic_exchange_routingkey";
	
	/*Spr direct*/
	public static final String SprDirectQueue="spr_direct_queue";
	public static final String SprDirectExchangeName="spr_direct_exchange_name";
	public static final String SprDirectExchangeRoutingKey="spr_direct_exchange_routingkey";
}

```

#### 3.核心配置文件

```properties
#Rabbit Config 
# IP RabbitIp   defaultValue locahost
spring.rabbitmq.host=127.0.0.1
#port RabbitPort defaultValue 5672
spring.rabbitmq.port=5672
#登录帐号密码
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
# vhost
spring.rabbitmq.virtualHost=/
#是否开启消息确认
spring.rabbitmq.publisherConfirms=true
#如果消息未发布到队列中触发returns
spring.rabbitmq.publisherReturns=true
#链接超时时间
spring.rabbitmq.connectionTimeout=3000
spring.rabbitmq.Cache.Channel.size=1
```

#### 4.完成消息的延迟发送





### 基于注解版消费者

#### 1.配置类

```java
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
```

#### 2.监听器1

```java
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

```

#### 3.监听器2

```java
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
```

#### 4.核心配置文件application.properties

```java
#Rabbit Config 
# IP RabbitIp   defaultValue locahost
spring.rabbitmq.host=127.0.0.1
#port RabbitPort defaultValue 5672
spring.rabbitmq.port=5672
#ç»å½å¸å·å¯ç 
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
# vhost
spring.rabbitmq.virtualHost=/
#æ¯å¦å¼å¯æ¶æ¯ç¡®è®¤
spring.rabbitmq.publisherConfirms=true
#å¦ææ¶æ¯æªåå¸å°éåä¸­è§¦åreturns
spring.rabbitmq.publisherReturns=true
#é¾æ¥è¶æ¶æ¶é´
spring.rabbitmq.connectionTimeout=3000
spring.rabbitmq.Cache.Channel.size=1
```

