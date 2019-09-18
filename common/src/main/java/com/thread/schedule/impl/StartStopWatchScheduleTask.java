package com.thread.schedule.impl;

import com.handler.ContextHolder;
import com.thread.schedule.ScheduleTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartStopWatchScheduleTask extends ScheduleTask{
	
	
	public StartStopWatchScheduleTask(String jobName,String groupName){
		super(jobName,groupName);
	}
	
	
	@Override
	public void execute(){
		ScheduleTask scheduleTask=ContextHolder.getScheduleTask();
		log.info(" 子类 打印一下，这个任务的key ={} jobName ={} group={}",scheduleTask.jobKey,scheduleTask.jobName,scheduleTask.jobGroup);
	}
}
