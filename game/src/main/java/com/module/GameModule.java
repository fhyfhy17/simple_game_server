package com.module;

import com.handler.ContextHolder;

public abstract class GameModule extends BaseModule{
	
	public void callBack(Runnable runnable){
		ContextHolder.callBack(runnable);
	}
}
