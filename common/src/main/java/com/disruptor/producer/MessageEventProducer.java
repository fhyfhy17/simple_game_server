package com.disruptor.producer;

import com.disruptor.*;
import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.RingBuffer;
import com.pojo.Packet;

public class MessageEventProducer {

    private final static EventTranslatorVararg<DisruptorEvent> translator =
            (event, seq, objs) -> event.setBaseEvent((BaseEvent) objs[0]);


    public static void publishMessage(DisruptorEnum type, Packet message) {
        RingBuffer ringBuffer = DisruptorManager.getRingBuffer(type);
        MessageEvent messageEvent = new MessageEvent(message);
        ringBuffer.publishEvent(translator, messageEvent);
    }
}
