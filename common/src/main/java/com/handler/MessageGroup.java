package com.handler;

import com.pojo.Packet;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class MessageGroup {

    private volatile boolean running = false;
    protected int handlerCount = 8; // 执行器数量
    private String name;
    public List<MessageThreadHandler> handlerList = new ArrayList<>();
    public List<MessageThreadHandler> unionHandlerList = new ArrayList<>();


    public MessageGroup(String name) {
        this.name = name;
    }

    public MessageGroup(String name, int handlerCount) {
        this.name = name;
        this.handlerCount = handlerCount;
    }

    public void startup() {
        // 正在运行
        if (running) {
            return;
        }

        // 开始启动
        running = true;

        // 初始化handler
        this.initHandlers();
    }

    public void initHandlers() {
        for (int i = 0; i < this.handlerCount; i++) {
            MessageThreadHandler handler = getMessageThreadHandler();
            handler.schedulerListInit();
            new Thread(handler, this.name + "-common-" + i).start();
            handlerList.add(handler);

            MessageThreadHandler unionHandler = getUnionMessageThreadHandler();
            if (unionHandler != null) {
                unionHandler.schedulerListInit();
                new Thread(unionHandler, this.name + "-union-" + i).start();
                unionHandlerList.add(unionHandler);
            }

        }

    }

    public abstract MessageThreadHandler getMessageThreadHandler();

    public abstract Object hashKey(Packet msg);

    public MessageThreadHandler getUnionMessageThreadHandler() {
        return null;
    }

    public void messageReceived(Packet msg) {

        // 分配执行器执行
        int index = Math.abs(hashKey(msg).hashCode()) % handlerCount;

        MessageThreadHandler handler = handlerList.get(index);
        handler.messageReceived(msg);
    }

}
