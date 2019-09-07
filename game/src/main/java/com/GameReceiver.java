package com;

import com.disruptor.DisruptorEnum;
import com.enums.TypeEnum;
import com.handler.GameMessageHandler;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.pojo.Packet;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class GameReceiver extends BaseReceiver {
    private MessageGroup m;

    private DisruptorEnum type = DisruptorEnum.GAME_MESSAGE;

    @PostConstruct
    public void startup() {
        m = new MessageGroup(TypeEnum.GroupEnum.GAME_GROUP.name()) {
            @Override
            public MessageThreadHandler getMessageThreadHandler() {
                return new GameMessageHandler();
            }
        };
        m.startup();

//        DisruptorCreater disruptorCreater = new DisruptorCreater(type.name(), MessageWorkerHandler.class);
//        disruptorCreater.create();
//        DisruptorManager.addDisruptor(type, disruptorCreater);
    }

    @Override
    public void onReceive(Packet message) {
//        MessageEventProducer.publishMessage(type, message);
        m.messageReceived(message);
    }

}
