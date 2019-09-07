package com.event;

import com.annotation.EventListener;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.util.SpringUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.Executors;


@Configuration
public class EventDispatcher {
    //TODO 异步事件还需要想  目前有并发问题

    private static EventBus eventBus;
    private static EventBus asyncEventBus;

    @PostConstruct
    public void init() {
        eventBus = new EventBus();
        asyncEventBus = new AsyncEventBus(Executors.newFixedThreadPool(10));
        Map<String, Object> eventListeners = SpringUtils.getBeansWithAnnotation(EventListener.class);
        if (MapUtils.isEmpty(eventListeners)) {
            return;
        }
        eventListeners.values().forEach(x -> eventBus.register(x));
    }

    public static <T extends PlayerEventData> void playerEventDispatch(T eventData) {
        eventBus.post(eventData);
    }

    public static <T extends PlayerEventData> void playerEventAsyncDispatch(T eventData) {
        asyncEventBus.post(eventData);
    }

    public static <T extends EventData> void eventDispatch(T eventData) {
        eventBus.post(eventData);
    }

    public static <T extends EventData> void eventAsyncdispatch(T eventData) {
        eventBus.post(eventData);
    }

}
