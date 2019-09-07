package com.disruptor.worker;

import com.disruptor.DisruptorEvent;
import com.lmax.disruptor.WorkHandler;

public class WorkerHandler implements WorkHandler<DisruptorEvent> {

    private String workerName;

    public WorkerHandler(String workerName) {
        this.workerName = workerName;
    }

    @Override
    public void onEvent(DisruptorEvent event) throws Exception {
        throw new RuntimeException("请使用子类实现");
    }
}