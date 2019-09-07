package com.net.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.handler.MessageThreadHandler;
import com.net.ConnectManager;
import com.net.msg.LOGIN_MSG;
import com.net.msg.Options;
import com.pojo.Packet;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GateToClientMessageHandler extends MessageThreadHandler {

    @Override
    public void pulse() {
        while (!pulseQueues.isEmpty()) {
            try {
                Packet message = pulseQueues.poll();

                dispatch(message);

            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    private void dispatch(Packet message) throws InvalidProtocolBufferException {
        ConnectManager connectManager = SpringUtils.getBean(ConnectManager.class);

        //如果是登录返回消息
        if (message.getId() == LOGIN_MSG.GTC_LOGIN.getDescriptor().getOptions().getExtension(Options.messageId)) {
            LOGIN_MSG.GTC_LOGIN m = LOGIN_MSG.GTC_LOGIN.parseFrom(message.getData());
            if (m.getResult().getResult()) {

                connectManager.register(m.getSessionId(), m.getUid());
                connectManager.writeToClient(m.getUid(), message);
                return;
            }
        }
        long uid = message.getUid();
        connectManager.writeToClient(uid, message);
    }

}
