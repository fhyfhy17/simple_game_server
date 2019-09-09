package com.thread.threadPool;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private String name;
    private boolean daemon = false;
    private AtomicInteger id = new AtomicInteger(1);

    /**
     * 创建 NamedThreadFactory 实例；
     *
     * @param name 名称；
     */
    public NamedThreadFactory(String name) {
        this.name = name;
    }

    /**
     * 创建 NamedThreadFactory 实例；
     *
     * @param name   名称；
     * @param daemon 是否守护线程；
     */
    public NamedThreadFactory(String name, boolean daemon) {
        this.name = name;
        this.daemon = daemon;
    }
    

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, name+"-"+id.incrementAndGet());
        thread.setDaemon(daemon);
        return thread;
    }
}
