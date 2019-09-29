package com.pojo;

import lombok.Data;

import java.util.concurrent.ConcurrentLinkedQueue;

@Data
public class BattlePlayer{
	private long playerId;
	
	private ConcurrentLinkedQueue<PacketWrapper> msgQueue = new ConcurrentLinkedQueue<>();
	

	public void addMessage(PacketWrapper packetWrapper) {
		msgQueue.add(packetWrapper);
	}
	
	public void removeMessage() {
		msgQueue.clear();
	}
}
