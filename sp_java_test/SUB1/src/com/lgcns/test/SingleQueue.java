package com.lgcns.test;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleQueue {
	
	private Queue<String> queue = null;
	
	public SingleQueue() {
		queue = new LinkedBlockingQueue<>();
	}
	public SingleQueue(int capacity) {
		queue = new LinkedBlockingQueue<>(capacity);
	}
	
	public boolean send(String msg) {
		return queue.offer(msg);
	}
	
	public String receive() {
		return queue.poll();
	}
}
