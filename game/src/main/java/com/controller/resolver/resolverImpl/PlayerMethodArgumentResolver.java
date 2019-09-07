package com.controller.resolver.resolverImpl;

import com.controller.resolver.ActionMethodArgumentResolver;
import com.controller.resolver.MethodParameter;
import com.exception.exceptionNeedSendToClient.NoPlayerException;
import com.pojo.Packet;
import com.pojo.Player;
import com.service.OnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PlayerMethodArgumentResolver implements ActionMethodArgumentResolver {
    @Autowired
    private OnlineService onlineService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Player.class.isAssignableFrom(parameter.getClassType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Packet message) throws Exception {
        Player playerByUid = onlineService.getPlayerByUid(message.getUid());
        if (Objects.isNull(playerByUid)) {
            throw new NoPlayerException("查询player时为空： uid为：" + message.getUid());
        }

        return playerByUid;

    }
}
