package com.handler;

import com.enums.TypeEnum;
import com.pojo.Packet;

public class GameMessageGroup extends MessageGroup{
	
	public GameMessageGroup(){
		super(TypeEnum.GroupEnum.GAME_GROUP.name());
	}
	
	public GameMessageGroup(String name){
		super(name);
	}
	
	public GameMessageGroup(String name,int handlerCount){
		super(name,handlerCount);
	}
	
	@Override
	public MessageThreadHandler getMessageThreadHandler(){
		return new GameMessageHandler();
	}
	
	@Override
	public Object hashKey(Packet msg){
		return msg.getUid();
	}
	
}
