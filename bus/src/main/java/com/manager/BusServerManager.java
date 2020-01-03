package com.manager;

import com.BaseVerticle;
import com.BusReceiver;
import com.BusVerticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BusServerManager extends ServerManager {

    @Autowired
    private BusVerticle verticle;

    @Autowired
    private BusReceiver busReceiver;

    @Override
    public BaseVerticle getVerticle() {
        return verticle;
    }

    @Override
    public void onServerStart() {
        super.onServerStart();
        busReceiver.start();
        //启动器计数
//        startWatch.count();
    }

    @Override
    public void asyncStart() {

    }

    @Override
    public void onServerStop() {
        super.onServerStop();
        log.info("停服完成 -------------------------------------------------------");
    }
    
    @Override
    public void startOver(){
    
    }
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        onServerStop();
    }
}
