package com.lgcns.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	private QueueMng qm = new QueueMng();
	private static final String CMD_SEND = "SEND";
	private static final String CMD_RECEIVE = "RECEIVE";
	private static final String CMD_CREATE = "CREATE";
	
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String inputMsg = "";
        String outputMsg = "";
        while (true) {
            try {
            	inputMsg = br.readLine();
				outputMsg = doOp(inputMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if(outputMsg != null) {
            	System.out.println(outputMsg);
            }
        }	
		
	}
	
	private String doOp(String msg) {
		String outputMsg = null;
		String[] arrMsg = msg.split(" ");
		String cmd = arrMsg[0];
		if(cmd.equals(CMD_SEND)) {
			if(qm.send(arrMsg[1], arrMsg[2]) == false) {
				outputMsg = "Queue Full";
			}
		} else if(cmd.equals(CMD_RECEIVE)) {
			outputMsg = qm.receive(arrMsg[1]);
		} else if(cmd.equals(CMD_CREATE)) {
			if(qm.createQueue(arrMsg[1], Integer.parseInt(arrMsg[2])) != null) {
				outputMsg = "Queue Exist";
			}
		}
		return outputMsg;
	}
}
