package com.handler;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class GameMessageHandler extends MessageThreadHandler {
	
	protected final ConcurrentLinkedQueue<Runnable> pulseSystemDisQueues= new ConcurrentLinkedQueue<>();
	
	public void systemDisReceived(Runnable runnable) {
		pulseSystemDisQueues.add(runnable);
	}
	
	@Override
	protected void tick(){
		super.tick();
		// 执行系统消息分发
		pulseSystemDisQueues();
	}
	
	/**
	 * 系统消息分发
	 * */
	public void pulseSystemDisQueues() {
		while (!pulseSystemDisQueues.isEmpty()) {
			try {
				Runnable poll=pulseSystemDisQueues.poll();
				poll.run();
			} catch (Throwable e) {
				log.error("系统消息分发报错", e);
			}
		}
	}
}
