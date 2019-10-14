package com.util;

import com.GameReceiver;
import com.pojo.Packet;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageThreadUtil{
	private static GameReceiver gameReceiver;
	
	public static void add(Packet packet){
		gameReceiver.onReceive(packet);
	}
	
	public static void addSystemDis(Long uid ,Runnable runnable){
		gameReceiver.systemDis(uid,runnable);
	}
	
	@Autowired
	public void setGameReceiver(GameReceiver gameReceiver){
		MessageThreadUtil.gameReceiver = gameReceiver;
	}
}
