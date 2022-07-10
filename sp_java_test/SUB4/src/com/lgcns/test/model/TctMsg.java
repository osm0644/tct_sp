package com.lgcns.test.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TctMsg implements Comparable<TctMsg>{

	private String msg = "";
	private String msgId = "";
	private long ctime = 0L;
	private AtomicInteger failCnt = new AtomicInteger();
	
	public TctMsg(String msg) {
		this.msg = msg;
		this.msgId = UUID.randomUUID().toString();
		this.ctime = System.currentTimeMillis();
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}
	
	public int increaseFailCnt() {
		return this.failCnt.incrementAndGet();
	}
	
	public int getFailCnt() {
		return failCnt.get();
	}

	public void resetCtime() {
		this.ctime = System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		return "TctMsg [msg=" + msg + ", msgId=" + msgId + ", ctime=" + ctime + ", failCnt=" + failCnt + "]";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof TctMsg == false) {
			return false;
		}
		TctMsg other = (TctMsg)o;
		return msgId.equals(other.getMsgId());
	}

	@Override
	public int compareTo(TctMsg o) {
		if(o.ctime < ctime) {
			return 1;
		} else if(o.ctime > ctime) {
			return -1;
		}
		return 0;
	}
	
	// for debug
	public static void main(String[] args) {
		Random random = new Random();
		int makeCnt = 5;
		List<TctMsg> list = new ArrayList<>();
		TctMsg msg = null;
		for(int i=0;i<makeCnt;i++) {
			msg = new TctMsg("A"+(100-i));
			msg.setCtime(msg.getCtime()+random.nextLong());
					
			list.add(msg);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		
		for(TctMsg m: list) {
			System.out.println(m);			
		}
		Collections.sort(list);
		System.out.println("=============");
		for(TctMsg m: list) {
			System.out.println(m);			
		}
		
	}
}
