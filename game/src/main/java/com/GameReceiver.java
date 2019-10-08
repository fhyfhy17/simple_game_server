package com;

import com.disruptor.DisruptorEnum;
import com.handler.GameMessageGroup;
import com.handler.MessageGroup;
import com.pojo.Packet;
import org.springframework.stereotype.Component;


@Component
public class GameReceiver extends BaseReceiver {
    private MessageGroup m;

    private DisruptorEnum type = DisruptorEnum.GAME_MESSAGE;

    @Override
    public void start() {
        m = new GameMessageGroup();
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
