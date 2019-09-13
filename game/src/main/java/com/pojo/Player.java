package com.pojo;

import com.module.*;
import lombok.Data;

import java.util.List;

@Data
public class Player {

    private long playerId;
    private long uid;

    private PlayerModule playerModule;
    private BagModule bagModule;
    private NoCellBagModule noCellBagModule;
    private MailModule mailModule;


    private List<BaseModule> modules;

    public Player() {
    }

    public void initParts(List<BaseModule> modules) {
        this.modules = modules;

        modules.forEach(x -> {
            x.setPlayer(this);
            x.onLoad();

        });
    }


    public void onDaily() {
        modules.forEach(BaseModule::onDaily);
    }

    public void onLogin() {
        modules.forEach(BaseModule::onLogin);
    }

    public void onLogout() {
        modules.forEach(BaseModule::onLogout);
    }

    public void onActivityOpen() {
        modules.forEach(BaseModule::onActivityOpen);
    }

    public void onActivityClose() {
        modules.forEach(BaseModule::onActivityClose);
    }

    public void onActivityReset() {
        modules.forEach(BaseModule::onActivityReset);
    }

    public void onLevelUp() {
        modules.forEach(BaseModule::onLevelUp);
    }

    public void onSecond() {
        modules.forEach(BaseModule::onSecond);
    }
}
