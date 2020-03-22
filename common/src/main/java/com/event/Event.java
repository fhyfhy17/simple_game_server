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
                    List<Pair<Method, Object>> pairs = map.get(eventType);
                    if (pairs.size() > 0) {
                        Pair<Method, Object> first = pairs.get(0);
                        if (!compareMethod(first.getKey(), method)) {
                            System.exit(0);
                        }
                    }
                    pairs.add(new Pair<>(method, value));

                } else {
                    List<Pair<Method, Object>> list = new ArrayList<>();
                    map.put(eventType, list);
                    list.add(new Pair<>(method, value));
                }

            }
        }
    }

    private static boolean compareMethod(Method methodFirst, Method methodOther) {
        int methodFirstParameterCount = methodFirst.getParameterCount();
        if (methodFirstParameterCount != methodOther.getParameterCount()) {
            log.error("同一事件的参数个数不匹配，\n" +
                            "方法1名字:{}  参数:{}\n" +
                            "方法2名字:{}  参数:{}", methodFirst.getDeclaringClass() + "." + methodFirst.getName()
                    , methodFirst.getParameters(), methodOther.getDeclaringClass() + "." + methodOther.getName()
                    , methodOther.getParameters());
            return false;
        }

        for (int i = 0; i < methodFirstParameterCount; i++) {
            if (methodFirst.getParameters()[i].getType().isAssignableFrom(methodOther.getParameters()[i].getType())) {
                log.error("同一事件的参数不匹配，\n" +
                                "方法1名字:{}  参数:{}\n" +
                                "方法2名字:{}  参数:{}", methodFirst.getDeclaringClass() + "." + methodFirst.getName()
                        , methodFirst.getParameters(), methodOther.getDeclaringClass() + "." + methodOther.getName()
                        , methodOther.getParameters());
                return false;
            }
        }
        return true;
    }

    //不想做过多的类继承，所以采用了可变参数，优点是不用写很多类，缺点是重构参数时，没有IDE红线提醒
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
