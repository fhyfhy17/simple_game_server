package com.disruptor;

import com.disruptor.worker.WorkerHandler;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class DisruptorCreator {
    private String name;
    private ProducerType defaultProducerType = ProducerType.SINGLE;
    private int defaultRingBufferSize = 128 * 128;
    private int defaultWorkerHandlersCount = 1;
    private Class<? extends WorkerHandler> workerHandler;
    private RingBuffer<DisruptorEvent> ringBuffer;

    public DisruptorCreator(String name, Class<? extends WorkerHandler> workerHandler) {
        this.name = name;
        this.workerHandler = workerHandler;
    }

    public DisruptorCreator(String name, int ringBufferSize, int workerHanlersCount, Class<? extends WorkerHandler> workerHandler) {
        this.name = name;
        this.defaultRingBufferSize = ringBufferSize;
        this.defaultWorkerHandlersCount = workerHanlersCount;
        this.workerHandler = workerHandler;
    }

    public void create() {

        List<WorkerHandler> workerHandlers = new ArrayList<>();

        for (int i = 0; i < defaultWorkerHandlersCount; i++) {
            Constructor<? extends WorkerHandler> constructor = null;
            WorkerHandler workerHandler = null;
            try {
                constructor = this.workerHandler.getConstructor(String.class);
                workerHandler = constructor.newInstance("workerHandler-" + name + "-" + i);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }

            workerHandlers.add(workerHandler);
        }

        Disruptor<DisruptorEvent> eventDisruptor = new Disruptor<>(
                DisruptorEvent::new,
                defaultRingBufferSize,
                new NamedThreadFactory(name),
                defaultProducerType,
                new BusySpinWaitStrategy());

        WorkerHandler[] ws = new WorkerHandler[workerHandlers.size()];
        eventDisruptor.handleEventsWithWorkerPool(workerHandlers.toArray(ws));

        ringBuffer = eventDisruptor.start();


    }

    public RingBuffer getRingBuffer() {
        return ringBuffer;
    }

    public String getName() {
        return name;
    }
}
