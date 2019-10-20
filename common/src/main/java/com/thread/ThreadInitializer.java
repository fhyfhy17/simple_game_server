package com.thread;

import com.alibaba.ttl.threadpool.TtlExecutors;
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
    public Executor getOrderingExecutor() {
        ThreadPool ioThreadPool = new ThreadPool(4,
                4,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("IO 线程", true));
            
        return TtlExecutors.getTtlExecutor(new ThreadOrderingExecutor(ioThreadPool));
    }
    
    @Bean(name = "saveDbThreadPool")
    public Executor getSaveDb() {
        ThreadPool saveBbThreadPool = new ThreadPool(4,
                4,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("saveDb 线程", true));
        return saveBbThreadPool;
    }

    @Bean(name = "callBackForMessageThread")
    public Executor getCallBackExecutor() {
        return TtlExecutors.getTtlExecutor(new CallBackForMessageThreadExecutor());
    }
}
