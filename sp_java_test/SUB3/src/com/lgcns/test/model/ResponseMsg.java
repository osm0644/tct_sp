package com.lgcns.test.model;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ResponseMsg {
	public static final String RST_OK = "Ok";
	public static final String RST_NO_MESSAGE = "No Message";
	public static final String RST_QUEUE_EXIST = "Queue Exist";
	public static final String RST_QUEUE_FULL = "Queue Full";
	public static final String RST_QUEUE_NOTFOUND = "Queue NotFound";
	
	@SerializedName("Result")
	private String result = RST_OK;
	
	@SerializedName("MessageID")
	private String msgId = null;
	
	@SerializedName("Message")
	private String msg = null;
	
	public ResponseMsg() {
		
	}
	public ResponseMsg(String result) {
		this.result = result;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
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
	@Override
	public String toString() {
		return "ResponseMsg [result=" + result + ", msgId=" + msgId + ", msg=" + msg + "]";
	}
	
	public String toJsonString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		//for debug
		ResponseMsg resMsg = new ResponseMsg(ResponseMsg.RST_NO_MESSAGE);
		resMsg.setMsgId("1234");
		resMsg.setMsg("Aha");
		
		System.out.println(resMsg.toJsonString());
		Gson gson = new Gson();
		Map map = gson.fromJson(resMsg.toJsonString(), Map.class);
		System.out.println(map);
	}
}
