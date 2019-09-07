package com.pojo;

import com.module.*;
import com.util.SpringUtils;
import lombok.Data;
import org.ehcache.CacheManager;

import java.util.List;

@Data
public class Player {

    private long playerId;
    private long uid;

    public PlayerModule playerPart;
    public BagModule bagPart;
    public NoCellBagModule noCellBagPart;
    public MailModule mailPart;

    private CacheManager cacheManager;

    private List<BaseModule> parts;

    public Player() {
        cacheManager = SpringUtils.getBean(CacheManager.class);
    }

    public void initParts(List<BaseModule> parts) {
        this.parts = parts;

        parts.forEach(x -> {
            x.setPlayer(this);
            x.onLoad();

        });
    }


    public void onDaily() {
        parts.forEach(BaseModule::onDaily);
    }

    public void onLogin() {
        parts.forEach(BaseModule::onLogin);
    }

    public void onLogout() {
        parts.forEach(BaseModule::onLogout);
    }

    public void onActivityOpen() {
        parts.forEach(BaseModule::onActivityOpen);
    }

    public void onActivityClose() {
        parts.forEach(BaseModule::onActivityClose);
    }

    public void onActivityReset() {
        parts.forEach(BaseModule::onActivityReset);
    }

    public void onLevelUp() {
        parts.forEach(BaseModule::onLevelUp);
    }

    public void onSecond() {
        parts.forEach(BaseModule::onSecond);
    }
}
