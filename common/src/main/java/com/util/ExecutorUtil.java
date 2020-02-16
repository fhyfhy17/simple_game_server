package com.util;

import com.esotericsoftware.minlog.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Component
public class ExecutorUtil {
    private static Map<String, Executor> map = new HashMap<>();

    public static Executor getExecutorByName(String name) {
        Executor executor = map.get(name);
        if (executor == null) {
            Log.error("Executor getExecutorByName error  name={}", name);
        }
        return executor;
    }

    @Autowired
    public void setMap(Map<String, Executor> map) {
        ExecutorUtil.map = map;
    }
}
