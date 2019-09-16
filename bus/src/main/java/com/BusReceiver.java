package com;

import com.enums.TypeEnum;
import com.handler.BusMessageHandler;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.pojo.Packet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
@Slf4j
public class BusReceiver extends BaseReceiver {
    private MessageGroup m;


    @PostConstruct
    public void startup() {
        m = new MessageGroup(TypeEnum.GroupEnum.BUS_GROUP.name(), 1) {
            @Override
            public MessageThreadHandler getMessageThreadHandler() {
                return new BusMessageHandler();
            }

            @Override
            public void messageReceived(Packet msg) {

                // 分配执行器执行
                MessageThreadHandler handler = handlerList.get(0);
                handler.messageReceived(msg);
            }


        };
        m.startup();

    }

    @Override
    public void onReceive(Packet message) {
        m.messageReceived(message);
    }

}
