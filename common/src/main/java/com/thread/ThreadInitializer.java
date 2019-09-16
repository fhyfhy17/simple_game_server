package com.thread;

import com.thread.threadPool.NamedThreadFactory;
import com.thread.threadPool.ThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadInitializer {

    @Bean(name = "ioThreadPool")
    public ThreadOrderingExecutor getOrderingExecutor() {
        ThreadPool ioThreadPool = new ThreadPool(4,
                4,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("IO 线程", true));

        return new ThreadOrderingExecutor(ioThreadPool);
    }
    
    @Bean(name = "saveDbThreadPool")
    public Executor getSaveDb() {
        ThreadPool saveBbThreadPool = new ThreadPool(4,
                4,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("saveDb 线程", false));
        return saveBbThreadPool;
    }

    @Bean(name = "singleScheduleTaskThreadPool")
    public Executor getMessageDealThreadPool() {
        ThreadPool singleScheduleTaskThreadPool = new ThreadPool(1,
                1,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("singleScheduleTask 线程", true));
        return singleScheduleTaskThreadPool;
    }
}
