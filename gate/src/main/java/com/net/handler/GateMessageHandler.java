package com.net.handler;

import com.enums.TypeEnum;
import com.handler.MessageThreadHandler;
import com.manager.ServerInfoManager;
import com.net.ConnectManager;
import com.net.Session;
import com.pojo.Packet;
import com.util.ContextUtil;
import com.util.RouteUtil;
import com.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GateMessageHandler extends MessageThreadHandler {
    @Override
    public void pulse() {
        while (!pulseQueues.isEmpty()) {
            try {
                Packet message = pulseQueues.poll();
                final long uid = message.getUid();
                final int cmdId = message.getId();
                dispatch(message);

            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    private void dispatch(Packet message) {
        message.setFrom(ContextUtil.id);
        message.setGate(ContextUtil.id);
        int msgId = message.getId();
        switch (RouteUtil.route(msgId)) {
            case LOGIN:
                String loginServerId = ServerInfoManager.hashChooseServer(message.getUid(), TypeEnum.ServerTypeEnum.LOGIN);
                if (StringUtils.isEmpty(loginServerId)) {
                    log.error("没有发现loginServer");
                    return;
                }
                ServerInfoManager.sendMessage(loginServerId, message);
                break;
            case GAME:
                ConnectManager connectManager = SpringUtils.getBean(ConnectManager.class);
                Session session = connectManager.getUserIdToConnectMap().get(message.getUid());
                ServerInfoManager.sendMessage(session.getGameId(), message);
                break;
            case X:
                break;
            default:
                break;
        }
    }

}
