package com.manager;

import com.BaseVerticle;
import com.GameReceiver;
import com.GameVerticle;
import com.lock.zk.ZkManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GameServerManager extends ServerManager {

    @Autowired
    protected GameVerticle verticle;

    @Autowired
    private GameReceiver gameReceiver;

    @Override
    public BaseVerticle getVerticle() {
        return verticle;
    }

    @Override
    public void onServerStart() {
        super.onServerStart();
        gameReceiver.start();
        //启动器计数
        startWatch.count();
    }
    
    @Override
    public void onServerStop() {
        super.onServerStop();
        log.info("停服完成 -------------------------------------------------------");
    }
    
    @Override
    public void startOver(){
        try
        {
            ZkManager.initial("");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        onServerStop();
    }
}
