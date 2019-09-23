package com;

import com.enums.TypeEnum;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.hanlder.BattleMessageHandler;

public class BattleReceiver extends GameReceiver {

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
}
