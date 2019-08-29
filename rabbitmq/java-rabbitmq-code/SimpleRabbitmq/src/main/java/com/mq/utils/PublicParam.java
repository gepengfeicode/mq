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
	
	public static String RETURN_DIRECT_QUEUE="return_direct_queue";
	public static String RETURN_DIRECT_EXCHANGE_NAME="return_direct_exchange_name";
	public static String RETURN_DIRECT_EXCHANGE_ROUTINGKEY="return_direct_exchange_routingkey";
}
