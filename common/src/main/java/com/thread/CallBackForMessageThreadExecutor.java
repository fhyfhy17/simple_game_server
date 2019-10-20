package com.thread;

import com.handler.ContextHolder;
import com.thread.schedule.ScheduleTask;

import java.util.concurrent.Executor;

/**
 * Executor的封装，作用是将消息回调给MessageHandler的线程池, 现在借用了ThreadLocal和Schedule系统实现，如果发现有问题
 * 可以直接换成  MessageThreadHandler加一个普通的Runnable队列实现
 */
public class CallBackForMessageThreadExecutor implements Executor {

    @Override
    public void execute(Runnable task) {
        ContextHolder.getScheduleAble().scheduleOnce(new ScheduleTask() {
            @Override
            public void execute() {
                task.run();
            }
        }, 3);
    }
}