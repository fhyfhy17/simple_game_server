package com.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventBusNameCreater {


    private static Map<String, Integer> map = new HashMap<>();

    public static String getName(String oldName) {
        synchronized (EventBusNameCreater.class) {
            Integer count = map.get(oldName);
            String newName = "";
            if (Objects.isNull(count)) {

                map.put(oldName, 0);
                newName = oldName + "-" + 0;
                return newName;
            } else {
                map.put(oldName, map.get(oldName) + 1);
                return oldName + "-" + map.get(oldName);
            }
        }
    }
}
