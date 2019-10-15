package com.service;

import com.GameReceiver;
import com.util.SpringUtils;


public abstract class GameService extends BaseService{
	
	//系统消息分发，暂用于系统转到个人
	public void systemDis(Long uid,Runnable runnable){
		SpringUtils.getBean(GameReceiver.class).systemDis(uid,runnable);
	}
	
}
