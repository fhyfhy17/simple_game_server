package com;

import com.handler.MessageGroup;
import com.hanlder.BattleMessageGroup;
import com.pojo.Packet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BattleReceiver extends BaseReceiver {

    private MessageGroup m;
    
    @Override
    public void start() {
        m = new BattleMessageGroup();
        m.startup();

    }
    
    @Override
    public void onReceive(Packet message){
        m.messageReceived(message);
    }
}
