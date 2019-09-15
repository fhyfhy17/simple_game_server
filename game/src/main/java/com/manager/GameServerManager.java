package com.manager;

import com.BaseVerticle;
import com.GameVerticle;
import com.enums.TypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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
    }
}
