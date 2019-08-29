//package com.cn.mq.revc.listener;
//
//import org.springframework.amqp.rabbit.annotation.RabbitHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@RabbitListener(queues = {"spr_topic_queue"})
//public class MsgReceiver {
// 
//	 @RabbitHandler
//    public void process(String content) {
////        logger.info("接收处理队列A当中的消息： " + content);
//    	System.out.println("获取到的消息为:" + content);
//    }
// 
//}