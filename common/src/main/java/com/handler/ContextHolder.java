package com.handler;

import com.Constant;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.pojo.Param;
import com.thread.schedule.ScheduleAble;
import com.thread.schedule.ScheduleTask;

import java.util.Objects;

public class ContextHolder {


    private static final TransmittableThreadLocal<Param> contextThreadLocal = new TransmittableThreadLocal<>();

    public static ScheduleAble getScheduleAble() {
        Param param = getParam();
        return param.get(Constant.CONTEXT_SCHEDULE_ABLE);
    }

    public static void setScheduleAble(ScheduleAble scheduleAble) {
        Param param = getParam();
        param.put(Constant.CONTEXT_SCHEDULE_ABLE, scheduleAble);
    }
    
    public static ScheduleTask getScheduleTask() {
        Param param = getParam();
        return param.get(Constant.CONTEXT_SCHEDULE_TASK);
    }
    
    public static void setScheduleTask(ScheduleTask scheduleTask) {
        Param param = getParam();
        param.put(Constant.CONTEXT_SCHEDULE_TASK, scheduleTask);
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
