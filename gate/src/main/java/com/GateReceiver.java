package com;

import com.enums.TypeEnum;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.net.handler.GateToClientMessageHandler;
import com.pojo.Packet;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GateReceiver extends BaseReceiver {
    private MessageGroup m;

    @PostConstruct
    public void startup() {
        m = new MessageGroup(TypeEnum.GroupEnum.GATE_TO_CLIENT_GROUP.name()) {
            @Override
            public MessageThreadHandler getMessageThreadHandler() {
                return new GateToClientMessageHandler();
            }
        };
        m.startup();
    }

    @Override
    public void onReceive(Packet message) {
        //这收到其它服务器返回消息直接刷到前端了
        m.messageReceived(message);
    }
}
