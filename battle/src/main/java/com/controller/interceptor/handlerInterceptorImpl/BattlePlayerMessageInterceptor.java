package com.controller.interceptor.handlerInterceptorImpl;

import com.annotation.Controllor;
import com.controller.ControllerHandler;
import com.controller.interceptor.HandlerInterceptor;
import com.exception.StatusException;
import com.pojo.BattlePlayer;
import com.pojo.Packet;
import com.pojo.PacketWrapper;
import com.service.BattleOnlineService;
import com.template.templates.type.TipType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@Slf4j
//玩家的消息转给玩家，不走默认的消息执行
//玩家的消息转给玩家，现在写在Controllor上了，可以考虑写在protobuf 或者有更好的区别方式
//TODO 暂时规定，玩家的消息都是protobuf，服务器间都是rpc
public class BattlePlayerMessageInterceptor implements HandlerInterceptor {
    @Autowired
    private BattleOnlineService battleOnlineService;
    
    @Override
    public boolean preHandle(Packet message,ControllerHandler handler,Object[] m){
        Controllor annotation=handler.getClass().getAnnotation(Controllor.class);
        if(annotation.isPlayerPacket()){
            BattlePlayer battlePlayer = battleOnlineService.getPlayerByUid(message.getUid());
            if(battlePlayer==null){
                throw new StatusException(TipType.NoBattlePlayer);
            }
            battlePlayer.addMessage(new PacketWrapper(message,handler,m));
            return false;
        }
        return true;
    }
}
