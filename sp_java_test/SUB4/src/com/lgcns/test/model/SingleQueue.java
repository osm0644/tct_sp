package com.lgcns.test.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SingleQueue {
		
	private String name = "";
	private Queue<TctMsg> queue = null;
	private Queue<TctMsg> deadQueue = null;
	private Map<String, TctMsg> mapHandling = new ConcurrentHashMap<>();
	
	private long processTimeout = 0;
	private int maxFailCount = 0;
	private int waitTime = 0;
	
	final Lock lock = new ReentrantLock(true);
	final Condition notEmpty = lock.newCondition();
	
	public SingleQueue(String name) {
		this.name = name;
		queue = new LinkedBlockingQueue<>();
	}
	public SingleQueue(String name, int capacity, int processTimeout, int maxFailCount, int waitTime) {
		this.name = name;
		queue = new LinkedBlockingQueue<>(capacity);
		deadQueue = new LinkedBlockingQueue<>();
		this.processTimeout = processTimeout * 1000;
		this.maxFailCount = maxFailCount;
		this.waitTime = waitTime;
	}
	
	public boolean send(String msg) {
		lock.lock();
		try {
			notEmpty.signal();
			return queue.offer(new TctMsg(msg));
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 
	 * @return null: ¾øÀ¸¸é 
	 */
	public TctMsg receive() {
		lock.lock();
		try {
			if(waitTime > 0 && 
					(queue.size()-mapHandling.size()) == 0) {				
				System.out.println(queue.size()+"|"+mapHandling.size()+"|"+this.waitTime+"|"+queue.peek());
				System.out.println(new Date()+"|"+name+"["+this+"]: wait");				
				boolean ret = notEmpty.await(waitTime, TimeUnit.SECONDS);
				System.out.println(queue.size()+"|"+mapHandling.size()+"|"+this.waitTime);
				System.out.println(new Date()+"|"+name+"["+this+"]: done."+ret);
			}
			TctMsg tmpMsg = null;
			TctMsg retMsg = null;
			Iterator<TctMsg> iter = queue.iterator();
			while(iter.hasNext()) {
				tmpMsg = iter.next();
				if(mapHandling.containsKey(tmpMsg.getMsgId()) == false) {
					tmpMsg.resetCtime();
					mapHandling.put(tmpMsg.getMsgId(), tmpMsg);
					retMsg = tmpMsg;
					break;
				}
			}
			return retMsg;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			lock.unlock();
		}
	}
	
	public void ack(String msgId) {
		TctMsg msg = mapHandling.get(msgId);
		if(msg == null) {
			return;
		}
		queue.remove(msg);
		mapHandling.remove(msgId);
	}
	
	public void fail(String msgId) {
		lock.lock();
		try {
			System.out.println("AA"+queue.size()+"|"+mapHandling.size());
			TctMsg msg = mapHandling.remove(msgId);			
			if(msg == null) {
				return;
			}
			int failCount = msg.increaseFailCnt();
			if(failCount > maxFailCount) {
				deadQueue.offer(msg);
				queue.remove(msg);
			} else {
				System.out.println("RESTORE:"+msg);
				System.out.println("BB"+queue.size()+"|"+mapHandling.size());
				notEmpty.signal();
			}
		} finally {
			lock.unlock();
		}
	}
	
	private void failAll(List<TctMsg> listRemove) {
		Collections.sort(listRemove);
		for(TctMsg msg: listRemove) {
			fail(msg.getMsgId());
		}
	}
	
	public TctMsg dlq() {
		return deadQueue.poll();
	}
	
	public void checkTimeout() {
		if(processTimeout <= 0) {
			return;
		}
		lock.lock();
		try {
			List<TctMsg> listRemove = new ArrayList<>();
			Iterator<String> iter = mapHandling.keySet().iterator();
			String msgId = "";
			TctMsg msg = null;
			long currtime = System.currentTimeMillis();
			while(iter.hasNext()) {
				msgId = iter.next();
				msg = mapHandling.get(msgId);
				if(currtime - msg.getCtime() > processTimeout) {
					listRemove.add(msg);
				}
			}
			if(listRemove.size() > 0) {
				failAll(listRemove);
			}
		} finally {
			lock.unlock();
		}
	}
}
