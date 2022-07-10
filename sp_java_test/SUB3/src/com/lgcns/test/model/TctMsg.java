package com.lgcns.test.model;

import java.util.UUID;

public class TctMsg {

	private String msg = "";
	private String msgId = "";
	
	public TctMsg(String msg) {
		this.msg = msg;
		this.msgId = UUID.randomUUID().toString(); 
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

	@Override
	public String toString() {
		return "TctMsg [msg=" + msg + ", msgId=" + msgId + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof TctMsg == false) {
			return false;
		}
		TctMsg other = (TctMsg)o;
		return msgId.equals(other.getMsgId());
	}
}
