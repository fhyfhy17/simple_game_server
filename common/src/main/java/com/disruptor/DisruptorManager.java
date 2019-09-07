package com.disruptor;

import com.lmax.disruptor.RingBuffer;

import java.util.HashMap;
import java.util.Map;

public class DisruptorManager {
    private static Map<DisruptorEnum, DisruptorCreator> disMap = new HashMap<>();

    public static DisruptorCreator getDisruptorCreator(DisruptorEnum type) {
        return disMap.get(type);
    }

    public static RingBuffer getRingBuffer(DisruptorEnum type) {
        DisruptorCreator disruptorCreator = disMap.get(type);
        if (disruptorCreator == null) {
            return null;
        }
        return disruptorCreator.getRingBuffer();
    }

    public static void addDisruptor(DisruptorEnum type, DisruptorCreator disruptorCreator) {
        disMap.put(type, disruptorCreator);
    }

}
