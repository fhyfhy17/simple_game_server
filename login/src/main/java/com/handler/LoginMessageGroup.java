package com.handler;

import com.enums.TypeEnum;
import com.google.protobuf.InvalidProtocolBufferException;
import com.net.msg.LOGIN_MSG;
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
		// 分配执行器执行
		if (msg.getId() == 10001) {
			try {
				return LOGIN_MSG.CTG_LOGIN.parseFrom(msg.getData()).getSessionId();
			} catch (InvalidProtocolBufferException e) {
				log.error("", e);
			}
		} else {
			return msg.getUid();
		}
		
		return 0;
	}
}
