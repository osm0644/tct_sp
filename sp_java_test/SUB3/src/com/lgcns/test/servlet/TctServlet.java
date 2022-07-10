package com.lgcns.test.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.lgcns.test.model.RequestMsg;

public abstract class TctServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final String BODYKEY_QUEUE_SIZE = "QueueSize";
	protected static final String BODYKEY_MSG = "Message";


	@SuppressWarnings("rawtypes")
	protected Map getBody(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		try (BufferedReader br = request.getReader()) {
			String buf = "";
			while( (buf = br.readLine()) != null) {
				sb.append(buf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Gson gson = new Gson();
		return gson.fromJson(sb.toString(), Map.class);
	}
	
	
	protected abstract RequestMsg getRequestMsg(HttpServletRequest request);
}
