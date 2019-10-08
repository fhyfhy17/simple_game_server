package com;


import com.handler.LoginMessageGroup;
import com.handler.MessageGroup;
import com.pojo.Packet;
import org.springframework.stereotype.Component;

@Component
public class LoginReceiver extends BaseReceiver {

    private MessageGroup m;

    @Override
    public void start() {
        m = new LoginMessageGroup();
        m.startup();
    }

    @Override
    public void onReceive(Packet message) {
        m.messageReceived(message);
    }
}
