package com.lgcns.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.lgcns.test.model.RequestMsg;
import com.lgcns.test.model.ResponseMsg;
import com.lgcns.test.model.TctMsg;
import com.lgcns.test.servlet.AckServlet;
import com.lgcns.test.servlet.CreateServlet;
import com.lgcns.test.servlet.FailServlet;
import com.lgcns.test.servlet.ReceiveServlet;
import com.lgcns.test.servlet.SendServlet;

public class Main {
	
	public static final String CMD_CREATE = "CREATE";
	public static final String CMD_SEND = "SEND";
	public static final String CMD_RECEIVE = "RECEIVE";
	public static final String CMD_ACK = "ACK";
	public static final String CMD_FAIL = "FAIL";
	
	private QueueMng qm = new QueueMng();
	
	public void run() {
		
		Server server = createServer(8080);		
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResponseMsg doOp(RequestMsg reqMsg) {
		ResponseMsg repMsg = new ResponseMsg();
		int ret = 0;		
		switch (reqMsg.getCmd()) {
		case CMD_CREATE: 
			if(qm.createQueue(reqMsg.getQueueName(), reqMsg.getQueueSize()) != null) {
				repMsg.setResult(ResponseMsg.RST_QUEUE_EXIST);
			}
			break;
		case CMD_SEND: 
			ret = qm.send(reqMsg.getQueueName(), reqMsg.getMsg());
			if(ret == 1) {				
				repMsg.setResult(ResponseMsg.RST_QUEUE_NOTFOUND);
			} else if(ret == 2) {
				repMsg.setResult(ResponseMsg.RST_QUEUE_FULL);
			}
			break;
		case CMD_RECEIVE: 
			TctMsg tctMsg = qm.receive(reqMsg.getQueueName());
			if(tctMsg == null) {
				repMsg.setResult(ResponseMsg.RST_NO_MESSAGE);
			} else {
				repMsg.setMsg(tctMsg.getMsg());
				repMsg.setMsgId(tctMsg.getMsgId());
			}
			break;
		case CMD_ACK: 
			qm.ack(reqMsg.getQueueName(), reqMsg.getMsgId());
			break;
		case CMD_FAIL: 
			qm.fail(reqMsg.getQueueName(), reqMsg.getMsgId());
			break;
		default: 
			//do something...
			break;
		}		
		return repMsg;
	}
	
	private Server createServer(int port) {
		Server server = new Server(port);
		
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
		
		CreateServlet createServlet = new CreateServlet(this);
		ServletHolder createHolder = new ServletHolder(createServlet);
		context.addServlet(createHolder, createServletPath(CMD_CREATE));
		
		SendServlet sendServlet = new SendServlet(this);
		ServletHolder sendHolder = new ServletHolder(sendServlet);
		context.addServlet(sendHolder, createServletPath(CMD_SEND));
		
		ReceiveServlet receiveServlet = new ReceiveServlet(this);
		ServletHolder receiveHolder = new ServletHolder(receiveServlet);
		context.addServlet(receiveHolder, createServletPath(CMD_RECEIVE));
		
		AckServlet ackServlet = new AckServlet(this);
		ServletHolder ackHolder = new ServletHolder(ackServlet);
		context.addServlet(ackHolder, createServletPath(CMD_ACK));
		
		FailServlet failServlet = new FailServlet(this);
		ServletHolder failHolder = new ServletHolder(failServlet);
		context.addServlet(failHolder, createServletPath(CMD_FAIL));		
		
		server.setHandler(context);
			
		return server;
	}
	
	private String createServletPath(String cmd) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("/").append(cmd).append("/*");
		return strBuf.toString();
	}
	
	/* 참고 링크
	 * 
	 * https://www.eclipse.org/jetty/documentation/jetty-9/index.html#jetty-helloworld
	 * https://stackoverflow.com/questions/39421686/jetty-pass-object-from-main-method-to-servlet
	 */
}
