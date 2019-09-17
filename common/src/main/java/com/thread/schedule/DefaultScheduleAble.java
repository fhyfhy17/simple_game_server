package com.thread.schedule;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class DefaultScheduleAble extends ScheduleAble
{
	@Override
	public void pulseSchedule(){
		for(;;){
			if(!schedulerList.isEmpty()) {
				ScheduleTask poll = schedulerList.poll();
				if(Objects.isNull(poll)){
					continue;
				}
				poll.execute();
				try{
					Thread.sleep(10);
				}
				catch(InterruptedException e){
					log.error("",e);
				}
			}
		}
	
	}
}
