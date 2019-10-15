package com.service;

import com.GameReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class GameService extends BaseService{
	
	@Autowired
	protected GameReceiver gameReceiver;
	
	//系统消息分发，暂用于系统转到个人
	public void systemDis(Long uid,Runnable runnable){
		gameReceiver.systemDis(uid,runnable);
	}
	
}
