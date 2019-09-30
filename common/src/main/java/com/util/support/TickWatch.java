package com.util.support;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickWatch{
	private boolean running = false;		
	private long period = -1;				
	private long nextTime = -1;				
	private long startTime = -1;	
	
	public TickWatch(long period) {
		start(period);
	}
	
	public TickWatch(long timeStart,long period) {
		start(timeStart, period);
	}
	
	public TickWatch(long period,boolean immediate) {
		start(period, immediate);
	}
	
	public TickWatch(long timeStart,long period,boolean immediate) {
		start(timeStart, period, immediate);
	}
	
	/**
	 * 开始
	 * @param period 执行间隔
	 */
	public void start(long period) {
		start(period, false);
	}
	
	/**
	 * 开始
	 * @param period 执行间隔
	 */
	public void start(long timeStart, long period) {
		start(timeStart, period, false);
	}
	
	/**
	 * 开始
	 * @param period 执行间隔
	 * @param immediate 立即执行一次
	 */
	public void start(long period, boolean immediate) {
		long now;
		now = System.currentTimeMillis();
		start(now, period, immediate);
	}
	
	/**
	 * 开始
	 * @param period 执行间隔
	 * @param immediate 立即执行一次
	 */
	public void start(long timeStart, long period, boolean immediate) {
		if(period<=0){
			log.error("时间间隔不能小于0");
			return;
		}
		
		//时间间隔
		this.period = period;
		
		
		//是否立即执行
		if(immediate) {
			this.nextTime = timeStart;
		} else {
			this.nextTime = timeStart + period;
		}
		
		this.startTime = timeStart;
		this.running = true;
	}
	
	/**
	 * 停止
	 */
	public void stop() {
		running = false;
	}
	
	/**
	 *  一次时间到即停止
	 */
	public boolean reachOnce(long now) {
		//未初始化或已停止
		if(!running) {
			return false;
		}
		
		//未达到时间
		if(nextTime > now) {
			return false;
		}
		
		//达成一次后停止
		stop();
		
		return true;
	}
	
	/**
	 * 周期间隔时间已到
	 */
	public boolean reachPeriod(long now) {
		//未初始化或已停止
		if(!running) {
			return false;
		}
		
		//未达到时间
		if(nextTime > now) {
			return false;
		}
		
		//更新周期时间
		nextTime += period;
		
		return true;
	}
	
	/**
	 * 是否是开始状态
	 * @return
	 */
	public boolean isStarted() {
		return running; 
	}

	public long getPeriod() {
		return period;
	}
	
	/**
	 * 剩余时间
	 */
	public long getTimeLeft(long curr) {
	
		if(!running) {
			return 0;
		}
	
		if(nextTime <= curr) {
			return 0;
		}
		
		return nextTime - curr;
	}
	
	/**
	 * 重新计时
	 */
	public void reStart() {
		this.nextTime = System.currentTimeMillis() + period;
		this.running = true;
	}
	
	/**
	 * 强制设置下一时刻而不改变间隔
	 */
	public void setTimeNext(long timeNext) {
		this.nextTime = timeNext;
	}
	
	/**
	 * 延长nextTime
	 * @param extend
	 */
	public void extendTimeNext(int extend) {
		this.nextTime += extend;
	}
	
	/**
	 * 获得计时器开始时的时间
	 * @return
	 */
	public long getStartTime() {
		return startTime;
	}
	

}
