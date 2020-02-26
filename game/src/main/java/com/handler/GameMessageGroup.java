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
	
	/**
	 * 系统消息分发，暂用于系统转到个人
	 * */
	public void systemDispatch(Long uid,Runnable runnable){
		int index = Math.abs(uid.hashCode()) % handlerCount;
		MessageThreadHandler handler = handlerList.get(index);
		((GameMessageHandler)handler).systemDispatchReceived(runnable);
	}
}
