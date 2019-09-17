package com.module;

import com.entry.BaseEntry;
import com.pojo.Player;
import com.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.CrudRepository;

@Getter
@Setter
public abstract class BaseModule{

    protected Player player;

    public abstract void onLoad();

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public String getCacheName() {
        return StringUtil.cutByRemovePostfix(getName(), "Module") + "EntryCache";
    }

    public abstract BaseEntry getEntry();

    public abstract CrudRepository getRepository();
    
    public void onDaily() {

    }

    public void onSecond() {

    }

    public abstract void onLogin();

    public void onLogout() {

    }

    public void onActivityOpen() {

    }

    public void onActivityClose() {

    }

    public void onActivityReset() {

    }

    public void onLevelUp() {

    }
}
