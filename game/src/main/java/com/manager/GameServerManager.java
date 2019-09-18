package com.manager;

import com.BaseVerticle;
import com.GameVerticle;
import com.enums.TypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GameServerManager extends ServerManager {

    @Autowired
    protected GameVerticle verticle;

    @Override
    public BaseVerticle getVerticle() {
        return verticle;
    }

    @Override
    public void onServerStart() {
        super.onServerStart();
        setServerStatus(TypeEnum.ServerStatus.OPEN);
        //TODO 要改变zookeeper里的状态，不是open的不能发消息
    }
    
    @Override
    public void onServerStop() {
        super.onServerStop();
        log.info("停服完成 -------------------------------------------------------");
    }
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        onServerStop();
    }
}
