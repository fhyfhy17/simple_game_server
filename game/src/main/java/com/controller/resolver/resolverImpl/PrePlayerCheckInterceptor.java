package com.controller.resolver.resolverImpl;

import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerInterceptor;
import com.enums.TypeEnum;
import com.pojo.Packet;
import com.pojo.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Order(5)
@Component
@Slf4j
public class PrePlayerCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(Packet message,ControllerHandler handler,Object[] param){
        if(Objects.isNull(param)||param.length<1){
            return true;
        }
        for(Object o : param){
            if( o instanceof Player){
                 Player player = (Player)o;
                 if(player.getPlayerStatus()!=TypeEnum.PlayerStatus.ONLINE){
                     log.info("player 不在 online状态 ，消息丢掉 ");
                     return false;
                 }
            }
        }
        return true;
    }
}
