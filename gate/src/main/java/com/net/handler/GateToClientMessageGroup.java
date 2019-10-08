package com.net.handler;

import com.enums.TypeEnum;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.pojo.Packet;

public class GateToClientMessageGroup extends MessageGroup{
	
	public GateToClientMessageGroup(){
		super(TypeEnum.GroupEnum.GATE_TO_CLIENT_GROUP.name());
	}
	
	public GateToClientMessageGroup(String name){
		super(name);
	}
	
	public GateToClientMessageGroup(String name,int handlerCount){
		super(name,handlerCount);
	}
	
	@Override
	public MessageThreadHandler getMessageThreadHandler(){
		return new GateToClientMessageHandler();
	}
	
	@Override
	public Object hashKey(Packet msg){
		return msg.getUid();
	}
	
	
}
