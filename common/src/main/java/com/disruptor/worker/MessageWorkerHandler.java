package com.disruptor.worker;

import com.controller.ControllerFactory;
import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerExecutionChain;
import com.disruptor.DisruptorEvent;
import com.disruptor.MessageEvent;
import com.pojo.Packet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageWorkerHandler extends WorkerHandler {
    public MessageWorkerHandler(String workerName) {
        super(workerName);
    }

    @Override
    public void onEvent(DisruptorEvent event) throws Exception {
        MessageEvent messageEvent = (MessageEvent) event.getBaseEvent();
        Packet message = messageEvent.getMessage();
        try {
            final int cmdId = message.getId();

            ControllerHandler handler = ControllerFactory.getControllerMap().get(cmdId);
            if (handler == null) {
                throw new IllegalStateException("收到不存在的消息，消息ID=" + cmdId);
            }
            //拦截器前
            if (!HandlerExecutionChain.applyPreHandle(message, handler)) {
                return;
            }
            //针对method的每个参数进行处理， 处理多参数,返回result
            com.google.protobuf.Message result = (com.google.protobuf.Message) handler.invokeForController(message);
            //拦截器后
            HandlerExecutionChain.applyPostHandle(message, result, handler);
        } catch (Exception e) {
            log.error("", e);
        }


    }
}
