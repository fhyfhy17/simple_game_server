package com.manager;

import com.BaseVerticle;
import com.GateReceiver;
import com.GateVerticle;
import com.net.ConnectManager;
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

    private NettyServer nettyServer;

    @Autowired
    private ConnectManager connectManager;

    @Autowired
    private GateReceiver gateReceiver;

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
            this.nettyServer = nettyServer;
        }).start();
        connectManager.startup(count);
        gateReceiver.startup(count);

    }
    
    @Override
    public void onServerStop() {
        this.nettyServer.stop();
        super.onServerStop();
        log.info("停服完成 -------------------------------------------------------");
    }
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        onServerStop();
    }
}
