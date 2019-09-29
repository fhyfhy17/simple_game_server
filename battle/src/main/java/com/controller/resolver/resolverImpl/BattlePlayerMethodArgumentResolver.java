package com.controller.resolver.resolverImpl;

import com.controller.resolver.ActionMethodArgumentResolver;
import com.controller.resolver.MethodParameter;
import com.exception.StatusException;
import com.pojo.BattlePlayer;
import com.pojo.Packet;
import com.service.BattleOnlineService;
import com.template.templates.type.TipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BattlePlayerMethodArgumentResolver implements ActionMethodArgumentResolver {
    @Autowired
    private BattleOnlineService battleOnlineService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return BattlePlayer.class.isAssignableFrom(parameter.getClassType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Packet message) throws Exception {
        BattlePlayer playerByUid = battleOnlineService.getPlayerByUid(message.getUid());
        if (Objects.isNull(playerByUid)) {
            throw new StatusException(TipType.NoBattlePlayer);
        }

        return playerByUid;

    }
}
