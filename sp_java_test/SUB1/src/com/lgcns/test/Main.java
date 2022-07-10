package com.lgcns.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	private SingleQueue sq = new SingleQueue();
	private static final String CMD_SEND = "SEND";
	private static final String CMD_RECEIVE = "RECEIVE";
	
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
		if(msg.startsWith(CMD_SEND)) {
			sq.send(msg.split(" ")[1]);
		} else if(msg.startsWith(CMD_RECEIVE)) {
			outputMsg = sq.receive();
		}		
		return outputMsg;
	}
}
