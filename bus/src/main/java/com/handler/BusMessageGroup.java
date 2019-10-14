package com.handler;

import com.enums.TypeEnum;
import com.pojo.Packet;

public class BusMessageGroup extends MessageGroup{
	
	public BusMessageGroup(){
		super(TypeEnum.GroupEnum.BUS_GROUP.name());
	}
	
	public BusMessageGroup(String name){
		super(name);
	}
	
	public BusMessageGroup(String name,int handlerCount){
		super(name,handlerCount);
	}
	
	@Override
	public MessageThreadHandler getMessageThreadHandler(){
		return new BusMessageHandler();
	}
	
	@Override
	public Object hashKey(Packet msg){
		return 0;
	}
	
	@Override
	public void messageReceived(Packet msg) {
		
		// 分配执行器执行
		MessageThreadHandler handler = handlerList.iterator().next();
		handler.messageReceived(msg);
	}
}
