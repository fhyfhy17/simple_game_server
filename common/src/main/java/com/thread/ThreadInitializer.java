package com.thread;

import com.thread.threadPool.NamedThreadFactory;
import com.thread.threadPool.ThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                new NamedThreadFactory("IO线程", true));

        return new ThreadOrderingExecutor(ioThreadPool);
    }

}
