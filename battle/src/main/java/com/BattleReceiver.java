package com;

import com.enums.TypeEnum;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.hanlder.BattleMessageHandler;
import com.pojo.Packet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BattleReceiver extends BaseReceiver {

    private MessageGroup m;
    
    @Override
    public void start() {
        m = new MessageGroup(TypeEnum.GroupEnum.BATTLE_GROUP.name()) {
            @Override
            public MessageThreadHandler getMessageThreadHandler() {
                return new BattleMessageHandler();
            }
        };
        m.startup();

    }
    
    @Override
    public void onReceive(Packet message){
    
    }
}
