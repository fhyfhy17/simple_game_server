package com.manager;

import com.BaseVerticle;
import com.GateVerticle;
import com.enums.TypeEnum;
import com.net.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GateServerManager extends ServerManager {

    @Autowired
    private GateVerticle verticle;

    @Override
    public BaseVerticle getVerticle() {
        return verticle;
    }

    @Override
    public void onServerStart() {
        super.onServerStart();
        //启动netty
        new Thread(()->{
            NettyServer nettyServer = new NettyServer();
            nettyServer.init(count);
        }).start();
        setServerStatus(TypeEnum.ServerStatus.OPEN);
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
