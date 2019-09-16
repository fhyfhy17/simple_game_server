package com.thread.schedule;

import com.pojo.Param;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.utils.Key;

import java.util.concurrent.ScheduledFuture;

/**
 * 时间调度任务
 */
public abstract class ScheduleTask {
	public static final int STATE_INITIALIZED = 0;		//新建实例
	public static final int STATE_WAITING = 1;			//等待执行
	public static final int STATE_INQUEUE = 2;			//执行队列中
	public static final int STATE_CANCEL = 3;				//已取消
	public static final int STATE_EXECUTING = 4;			//执行中
	public static final int STATE_DONE = 5;				//已执行完
	
	
	//cronSchedule调度相关内容
	public Scheduler sched;
	public JobKey jobKey;
	public ScheduledFuture<?> future;
	public Trigger trigger;
	private Param param;		//参数
	
	
	public int state;				//调度状态
	public long createdAt;			//创建时间

	public long triggerAt;			//触发时间,cron类型为0
	public long triggerPeriod;		//触发间隔,cron类型为0
	public String triggerCronStr;	//cron时间串，""表示非cron触发
	
	/**
	 * 执行调度任务
	 */
	public abstract void execute();
	
	/**
	 * 获取job名
	 */
	public String getJobName() {
		return null;
	}
	
	/**
	 * 获取group名
	 * @return
	 */
	public String getJobGroup() {
		return null;
	}
	
	public ScheduleTask(Object... params) {
		state = STATE_INITIALIZED;
		
		//设置参数
		param = new Param(params);
		//设置jobKey
		if(getJobName() == null) {
			jobKey = new JobKey(Key.createUniqueName(getJobGroup()), getJobGroup());
		} else {
			jobKey = JobKey.jobKey(getJobName(), getJobGroup());
		}
		
		//设置创建时间
		this.createdAt = System.currentTimeMillis();
	}
	
	public Param getParam() {
		return param;
	}
	
	/**
	 * 下次执行时间
	 * @return
	 */
	public long getNextExecuteTime() {
		//cron调度返回下次执行时间
		if(triggerCronStr!=null && !"".equals(triggerCronStr)) {
			return trigger.getNextFireTime().getTime();
		} else {
			//一般调度返回执行时间
			return triggerAt;
		}
	}
}
