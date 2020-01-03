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
        connectManager.start();
        gateReceiver.start();
        //启动器计数
//        startWatch.count();
    }

    @Override
    public void asyncStart() {
        //启动netty
        new Thread(() -> {
            nettyServer = new NettyServer();
            incrAsyncCount();
            nettyServer.init(this);
        }).start();
    }

    @Override
    public void onServerStop() {
        this.nettyServer.stop();
        super.onServerStop();
        log.info("停服完成 -------------------------------------------------------");
    }

    @Override
    public void startOver() {

    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        onServerStop();
    }
}
