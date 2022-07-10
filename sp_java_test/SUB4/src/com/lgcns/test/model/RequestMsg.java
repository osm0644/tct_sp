package com.lgcns.test.model;

public class RequestMsg {

	private String cmd = "";
	private String queueName = "";
	private String msgId = "";
	private String msg = "";
	private int queueSize = 1;
	private int processTimeout;
	private int maxFailCount;
	private int waitTime;
	
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getQueueSize() {
		return queueSize;
	}
	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}
	public int getProcessTimeout() {
		return processTimeout;
	}
	public void setProcessTimeout(int processTimeout) {
		this.processTimeout = processTimeout;
	}
	public int getMaxFailCount() {
		return maxFailCount;
	}
	public void setMaxFailCount(int maxFailCount) {
		this.maxFailCount = maxFailCount;
	}
	public int getWaitTime() {
		return waitTime;
	}
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
	@Override
	public String toString() {
		return "RequestMsg [cmd=" + cmd + ", queueName=" + queueName + ", msgId=" + msgId + ", msg=" + msg
				+ ", queueSize=" + queueSize + ", processTimeout=" + processTimeout + ", maxFailCount=" + maxFailCount
				+ ", waitTime=" + waitTime + "]";
	}
}
