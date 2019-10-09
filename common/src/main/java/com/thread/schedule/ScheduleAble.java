package com.thread.schedule;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.quartz.JobKey.jobKey;

@Slf4j
public abstract class ScheduleAble{
	protected ConcurrentLinkedQueue<ScheduleTask> schedulerList = new ConcurrentLinkedQueue<>(); // 待处理的时间调度队列
	//任务队列调度器
	public Scheduler scheduler;
	
	
	public void schedulerListInit() {
		try {
			this.scheduler = new StdSchedulerFactory().getScheduler();
			this.scheduler.start();
			
		} catch (SchedulerException e) {
			log.error("",e);
		}
	}
	
	
	/**
	 * 延迟delay毫秒之后执行任务
	 *
	 * @param task
	 * @param delay 毫秒
	 */
	public void scheduleOnce(ScheduleTask task, long delay) {
		task.triggerAt = System.currentTimeMillis() + delay;
		// 定义trigger
		SimpleScheduleBuilder sche = SimpleScheduleBuilder.repeatSecondlyForever();
		// 循环次数 设置为0 代表不多余循环只执行一次
		sche.withRepeatCount(0);
		// 添加任务
		schedule(task, sche, delay);
	}
	
	private void schedule(ScheduleTask task,ScheduleBuilder<?> scheduleBuilder,long delay) {
		try {
			// 开始执行时间
			Date startAt = new Date();
			if (delay > 0) {
				startAt = new Date(System.currentTimeMillis() + delay);
			}
			
			// 定义时间调度的job内容
			JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(task.jobKey).build();
			jobDetail.getJobDataMap().put("task", task);
			jobDetail.getJobDataMap().put("scheduler", schedulerList);
			
			// 创建最终trigger
			Trigger trigger = TriggerBuilder.newTrigger().startAt(startAt).withSchedule(scheduleBuilder).build();
			
			// 绑定job和trigger
			scheduler.scheduleJob(jobDetail, trigger);
			
			// 设置任务信息
			task.state = ScheduleTask.STATE_WAITING;
			task.sched = scheduler;
			task.jobKey = jobDetail.getKey();
			task.trigger = trigger;
		} catch (Exception e) {
			log.error("",e);
		}
	}
	
	public abstract void tickSchedule();
	/**
	 * 从指定的delay毫秒延迟之后，开始以重复的速率每period毫秒执行
	 *
	 * @param task
	 * @param delay
	 * @param period
	 */
	public void schedulePeriod(ScheduleTask task, long delay, long period) {
		task.triggerAt = System.currentTimeMillis() + delay;
		task.triggerPeriod = period;
		
		// 定义schedule
		SimpleScheduleBuilder sche = SimpleScheduleBuilder.repeatSecondlyForever();
		// 执行间隔
		sche.withIntervalInMilliseconds(period);
		
		// 添加任务
		schedule(task, sche, delay);
	}
	
	/**
	 * 添加时间任务队列 支持cron格式
	 *
	 * @param task
	 */
	public void scheduleCron(ScheduleTask task, String cronStr) {
		task.triggerCronStr = cronStr;
		
		// 定义schedule
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronStr);
		
		// 添加任务
		schedule(task, scheduleBuilder, 0);
	}
	
	
	/**
	 * 从指定的delay毫秒延迟之后，开始以重复的速率每period毫秒执行，执行count次
	 * @param task
	 * @param delay
	 * @param period
	 * @param count
	 */
	public void scheduleRepeatForTotalCount(ScheduleTask task, long delay, long period, int count) {
		task.triggerAt = System.currentTimeMillis() + delay;
		task.triggerPeriod = period;
		
		// 定义schedule
		SimpleScheduleBuilder sche = SimpleScheduleBuilder.repeatSecondlyForTotalCount(count);
		// 执行间隔
		sche.withIntervalInMilliseconds(period);
		
		// 添加任务
		schedule(task, sche, delay);
	}
	
	
	
	/**
	 * 删除所在port上默认jobGroup中指定jobName的job
	 * @param jobName
	 * @throws SchedulerException
	 */
	public void deleteSchedulerJob(String jobName) throws SchedulerException {
		scheduler.deleteJob(jobKey(jobName, null));
	}
	
	/**
	 * 删除所在port上的指定jobName和jobGroupName的job
	 * @param jobName
	 * @param jobGroupName
	 * @throws SchedulerException
	 */
	public void deleteSchedulerJob(String jobName, String jobGroupName) throws SchedulerException {
		scheduler.deleteJob(jobKey(jobName, jobGroupName));
	}
	
	/**
	 * 删除所在指定 jobKey 的 job
	 * @throws SchedulerException
	 */
	public void deleteSchedulerJob(JobKey jobKey) throws SchedulerException {
		scheduler.deleteJob(jobKey);
	}
	
	/**
	 * 通过group名删除所在port上的所有group内的scheduler
	 * @param jobGroupName
	 * @throws SchedulerException
	 */
	public void deleteSchedulerJobsByGroup(String jobGroupName) throws SchedulerException {
		//for (String group : port.scheduler.getJobGroupNames()) {
		//	if(!group.equals(jobGroupName)) continue;
		//	for(JobKey jobKey : port.scheduler.getJobKeys(GroupMatcher.jobGroupEndsWith(group))) {
		//		port.scheduler.deleteJob(jobKey);
		//	}
		//}
	}
}
