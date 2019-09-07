package com.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.net.msg.LOGIN_MSG;
import com.pojo.Packet;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class MessageGroup {

    private volatile boolean running = false;
    protected int handlerCount = 8; // 执行器数量
    private String name;
    public List<MessageThreadHandler> hanlderList = new ArrayList<>();
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

        // 初始化hanlder
        this.initHandlers();
    }
    
    public void initHandlers() {
        for (int i = 0; i < this.handlerCount; i++) {
            MessageThreadHandler handler = getMessageThreadHandler();
            new Thread(handler, this.name +"-common"+ i).start();
            hanlderList.add(handler);
            MessageThreadHandler unionHandler = getUnionMessageThreadHandler();
            if(unionHandler!=null){
                new Thread(handler, this.name+"-union" + i).start();
                unionHandlerList.add(handler);
            }
            
        }
    }

    public abstract MessageThreadHandler getMessageThreadHandler();
    
    public  MessageThreadHandler getUnionMessageThreadHandler(){
        return null;
    }
    
    public void messageReceived(Packet msg) {
        int index = 0;

        // 分配执行器执行
        if (msg.getId() == 10001) {
            try {
                index = Math.abs(LOGIN_MSG.CTG_LOGIN.parseFrom(msg.getData()).getSessionId().hashCode()) % handlerCount;
            } catch (InvalidProtocolBufferException e) {
                log.error("", e);
            }
        } else {
            index = (int) (Math.abs(msg.getUid()) % handlerCount);
        }

        MessageThreadHandler handler = hanlderList.get(index);
        handler.messageReceived(msg);
    }

}
