package com.hanlder;

import com.enums.TypeEnum;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.pojo.Packet;

public class BattleMessageGroup extends MessageGroup{
	
	public BattleMessageGroup(){
		super(TypeEnum.GroupEnum.BATTLE_GROUP.name());
	}
	
	public BattleMessageGroup(String name){
		super(name);
	}
	
	public BattleMessageGroup(String name,int handlerCount){
		super(name,handlerCount);
	}
	
	@Override
	public MessageThreadHandler getMessageThreadHandler(){
		return new BattleMessageHandler();
	}
	
	@Override
	public Object hashKey(Packet msg){
		return msg.getUid();
	}
}
