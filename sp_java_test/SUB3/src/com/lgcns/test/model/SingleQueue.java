package com.lgcns.test.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleQueue {
		
	private Queue<TctMsg> queue = null;
	private Map<String, TctMsg> mapHandling = new HashMap<>();
	
	public SingleQueue() {
		queue = new LinkedBlockingQueue<>();
	}
	public SingleQueue(int capacity) {
		queue = new LinkedBlockingQueue<>(capacity);
	}
	
	public boolean send(String msg) {
		return queue.offer(new TctMsg(msg));
	}
	
	/**
	 * 
	 * @return null: ¾øÀ¸¸é 
	 */
	public synchronized TctMsg receive() {
		TctMsg tmpMsg = null;
		TctMsg retMsg = null;
		Iterator<TctMsg> iter = queue.iterator();
		while(iter.hasNext()) {
			tmpMsg = iter.next();
			if(mapHandling.containsKey(tmpMsg.getMsgId()) == false) {
				mapHandling.put(tmpMsg.getMsgId(), tmpMsg);
				retMsg = tmpMsg;
				break;
			}
		}
		return retMsg;
	}
	
	public synchronized void ack(String msgId) {
		TctMsg msg = mapHandling.get(msgId);
		if(msg == null) {
			return;
		}
		queue.remove(msg);
		mapHandling.remove(msgId);
	}
	
	public synchronized void fail(String msgId) {
		mapHandling.remove(msgId);
	}
}
