package com.handler;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class GameMessageHandler extends MessageThreadHandler {
	
	protected final ConcurrentLinkedQueue<Runnable> tickSystemDisQueues= new ConcurrentLinkedQueue<>();
	
	public void systemDisReceived(Runnable runnable) {
		tickSystemDisQueues.add(runnable);
	}
	
	@Override
	protected void tick(){
		super.tick();
		// 执行系统消息分发
		tickSystemDisQueues();
	}
	
	/**
	 * 系统消息分发
	 * */
	public void tickSystemDisQueues() {
		while (!tickSystemDisQueues.isEmpty()) {
			try {
				Runnable poll=tickSystemDisQueues.poll();
				poll.run();
			} catch (Throwable e) {
				log.error("系统消息分发报错", e);
			}
		}
	}
}
