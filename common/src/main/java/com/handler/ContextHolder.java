package com.handler;

import com.Constant;
import com.pojo.Param;

import java.util.Objects;

public class ContextHolder {


    private static final ThreadLocal<Param> contextThreadLocal = new InheritableThreadLocal<>();

    public static ScheduleAble getScheduleAble() {
        Param param = getParam();
        return param.get(Constant.CONTEXT_SCHEDULE_ABLE);
    }

    public static void setScheduleAble(ScheduleAble scheduleAble) {
        Param param = getParam();
        param.put(Constant.CONTEXT_SCHEDULE_ABLE, scheduleAble);
    }

    public static Param getParam() {
        Param context = contextThreadLocal.get();
        if (Objects.isNull(context)) {
            context = new Param();
            contextThreadLocal.set(context);
        }
        return context;
    }

    public static void clear() {
        contextThreadLocal.remove();
    }

}
