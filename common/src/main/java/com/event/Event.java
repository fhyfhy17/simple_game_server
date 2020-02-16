package com.event;

import com.annotation.EventListener;
import com.annotation.EventMethod;
import com.enums.EventType;
import com.pojo.Pair;
import com.util.ExecutorUtil;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
public class Event {


    private static Map<EventType, List<Pair<Method, Object>>> map = new HashMap<>();

    public static void init() {
        Map<String, Object> eventListeners = SpringUtils.getBeansWithAnnotation(EventListener.class);
        if (MapUtils.isEmpty(eventListeners)) {
            return;
        }
        for (Object value : eventListeners.values()) {
            for (Method method : value.getClass().getMethods()) {
                EventMethod annotation = method.getAnnotation(EventMethod.class);
                if (annotation == null) {
                    continue;
                }
                EventType eventType = annotation.value();

                if (map.containsKey(eventType)) {
                    map.get(eventType).add(new Pair<>(method, value));
                } else {
                    List<Pair<Method, Object>> list = new ArrayList<>();
                    map.put(eventType, list);
                    list.add(new Pair<>(method, value));
                }

            }
        }
    }

    public static void post(EventType eventType, Object... objects) {
        List<Pair<Method, Object>> list = map.get(eventType);
        if (!CollectionUtils.isEmpty(list)) {
            for (Pair<Method, Object> pair : list) {
                Method method = pair.getKey();
                EventMethod annotation = method.getAnnotation(EventMethod.class);
                String executorName = annotation.executor();
                Executor executor = ExecutorUtil.getExecutorByName(executorName);
                if (executor == null) {
                    continue;
                }
                executor.execute(() -> {
                    try {
                        pair.getKey().invoke(pair.getValue(), objects);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });

            }
        }
    }

}
