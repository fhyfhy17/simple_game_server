package com.manager;

import com.BaseVerticle;
import com.BattleReceiver;
import com.BattleVerticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BattleServerManager extends ServerManager {

    @Autowired
    private BattleVerticle verticle;

    @Autowired
    private BattleReceiver battleReceiver;

    @Override
    public BaseVerticle getVerticle() {
        return verticle;
    }

    @Override
    public void onServerStart() {
        super.onServerStart();
        battleReceiver.start();
        //启动器计数
        startWatch.count();
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
