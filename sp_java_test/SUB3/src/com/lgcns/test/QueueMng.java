package com.lgcns.test;

import java.util.HashMap;
import java.util.Map;

import com.lgcns.test.model.SingleQueue;
import com.lgcns.test.model.TctMsg;

public class QueueMng {
	private Map<String, SingleQueue> mapQueue = new HashMap<>();
	
	
	public SingleQueue createQueue(String queueName, int capacity) {
		return mapQueue.putIfAbsent(queueName, new SingleQueue(capacity));
	}
	
	/**
	 * 
	 * @param queueName
	 * @param msg
	 * @return 0: 성공, 1: Queue없음, 2: QueueFull
	 */
	public int send(String queueName, String msg) {
		int ret = 1;
		if(mapQueue.containsKey(queueName)) {
			if(mapQueue.get(queueName).send(msg)) {
				ret = 0;
			} else {
				ret = 2;
			}
		}
		return ret;
	}
	
	public TctMsg receive(String queueName) {
		TctMsg msg = mapQueue.get(queueName).receive();
		return msg;
	}
	
	public void ack(String queueName, String msgId) {
		if(mapQueue.containsKey(queueName)) {
			mapQueue.get(queueName).ack(msgId);
		}
	}
	
	public void fail(String queueName, String msgId) {
		if(mapQueue.containsKey(queueName)) {
			mapQueue.get(queueName).fail(msgId);
		}	
	}
}
