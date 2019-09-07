package com.disruptor;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

    private String executorName;

    public NamedThreadFactory(String executorName) {
        this.executorName = executorName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        String name = executorName + " Thread - " + thread.getId();
        System.out.println(name);
        thread.setName(name);
        return thread;
    }

}