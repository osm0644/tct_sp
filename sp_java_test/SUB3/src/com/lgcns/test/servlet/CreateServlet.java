package com.lgcns.test.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lgcns.test.Main;
import com.lgcns.test.model.RequestMsg;
import com.lgcns.test.model.ResponseMsg;

public class CreateServlet extends TctServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Main m;
	
	public CreateServlet(Main m) {
		super();
		this.m = m;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
		
		RequestMsg reqMsg = getRequestMsg(request);
		Map mapBody = getBody(request);
		int queueSize = ((Double)mapBody.get(BODYKEY_QUEUE_SIZE)).intValue();
		reqMsg.setQueueSize(queueSize);
		
		ResponseMsg repMsg = m.doOp(reqMsg);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().println(repMsg.toJsonString());		
	}
	
	protected RequestMsg getRequestMsg(HttpServletRequest request) {
		RequestMsg reqMsg = new RequestMsg();
		reqMsg.setCmd(Main.CMD_CREATE);
		
		String[] arrPath = request.getPathInfo().split("/");
		reqMsg.setQueueName(arrPath[1]);		
		return reqMsg;
	}
}
