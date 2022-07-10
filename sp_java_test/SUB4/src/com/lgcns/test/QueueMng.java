package com.lgcns.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lgcns.test.model.SingleQueue;
import com.lgcns.test.model.TctMsg;

public class QueueMng {
	private Map<String, SingleQueue> mapQueue = new ConcurrentHashMap<>();
	private Thread timeoutThread = null;
	
	
	public void startTimeoutHandler() {
		timeoutThread = new TimeoutHandler("timeoutHandler", mapQueue);
		timeoutThread.setDaemon(true);
		timeoutThread.start();
	}
	
	public SingleQueue createQueue(String queueName, int capacity, 
			int processTimeout, int maxFailCount, int waitTime) {
		return mapQueue.putIfAbsent(queueName, 
				new SingleQueue(queueName, capacity, processTimeout, maxFailCount, waitTime));
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
	
	public TctMsg dlq(String queueName) {
		TctMsg msg = mapQueue.get(queueName).dlq();
		return msg;
	}
	
	private static class TimeoutHandler extends Thread {		
		private Map<String, SingleQueue> mapMonitor = null;
		
		public TimeoutHandler(String name, Map<String, SingleQueue> mapMonitor) {
			super(name);
			this.mapMonitor = mapMonitor;
		}
		
		@Override
		public void run() {
			System.out.println(this.getName()+" Thread start!!");
			while(true) {
				try {
//					System.out.println(this.getName()+" Monitor start!!");
					for(Map.Entry<String, SingleQueue> elem: mapMonitor.entrySet()) {
						elem.getValue().checkTimeout();
					}
//					System.out.println(this.getName()+" Monitor end!!");
					Thread.sleep(100L);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
