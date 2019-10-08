package com;

import com.handler.MessageGroup;
import com.net.handler.GateToClientMessageGroup;
import com.pojo.Packet;
import org.springframework.stereotype.Component;

@Component
public class GateReceiver extends BaseReceiver {
    private MessageGroup m;

    @Override
    public void start() {
        m = new GateToClientMessageGroup();
        m.startup();
    }

    @Override
    public void onReceive(Packet message) {
        //这收到其它服务器返回消息直接刷到前端了
        m.messageReceived(message);
    }
}
