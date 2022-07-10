package com.lgcns.test;

import java.util.HashMap;
import java.util.Map;

public class QueueMng {
	private Map<String, SingleQueue> mapQueue = new HashMap<>();
	
	public SingleQueue createQueue(String queueName, int capacity) {
		return mapQueue.putIfAbsent(queueName, new SingleQueue(capacity));
	}
	
	public boolean send(String queueName, String msg) {
		if(mapQueue.containsKey(queueName)) {
			return mapQueue.get(queueName).send(msg);
		}
		return true;
	}
	
	public String receive(String queueName) {
		return mapQueue.get(queueName).receive();
	}
}
