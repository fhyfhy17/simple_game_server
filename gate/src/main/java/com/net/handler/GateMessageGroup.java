package com.net.handler;

import com.enums.TypeEnum;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.pojo.Packet;

public class GateMessageGroup extends MessageGroup{
	
	public GateMessageGroup(){
		super(TypeEnum.GroupEnum.GATE_GROUP.name());
	}
	
	public GateMessageGroup(String name){
		super(name);
	}
	
	public GateMessageGroup(String name,int handlerCount){
		super(name,handlerCount);
	}
	
	@Override
	public MessageThreadHandler getMessageThreadHandler(){
		return new GateMessageHandler();
	}
	
	@Override
	public Object hashKey(Packet msg){
		return msg.getUid();
	}
	
	
}
