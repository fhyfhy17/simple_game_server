package com.handler;

import com.enums.TypeEnum;
import com.pojo.Packet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginMessageGroup extends MessageGroup{
	
	public LoginMessageGroup(){
		super(TypeEnum.GroupEnum.LOGIN_GROUP.name());
	}
	
	public LoginMessageGroup(String name){
		super(name);
	}
	
	public LoginMessageGroup(String name,int handlerCount){
		super(name,handlerCount);
	}
	
	@Override
	public MessageThreadHandler getMessageThreadHandler(){
		return new LoginMessageHandler();
	}
	
	@Override
	public Object hashKey(Packet msg){
		if (msg.getId() == 10001) {
			return msg.getRpc();
		} else {
			return msg.getUid();
		}
	}
}
