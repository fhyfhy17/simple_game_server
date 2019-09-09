package com.thread;

import java.util.concurrent.Executor;

/**
 * 有序线程池,根据提交任务的线程ID，进行区分
 */
public class ThreadOrderingExecutor extends OrderingExecutor {
    
    public ThreadOrderingExecutor(Executor delegate){
        super(delegate);
    }
    
    @Override
    public void execute(Runnable task){
        execute(task,Thread.currentThread().getName());
    }
}