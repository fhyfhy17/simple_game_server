package com.module;

import com.entry.BaseEntry;
import com.pojo.Player;
import com.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public abstract class BaseModule{

    @Autowired
    protected CacheManager cacheManager;

    protected Player player;

    public abstract void onLoad();

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public String getCacheName() {
        return StringUtil.cutByRemovePostfix(getName(), "Part") + "EntryCache";
    }

    public BaseEntry getEntry() {
        Cache<Long, BaseEntry> cache = cacheManager.getCache(getCacheName(), Long.class, BaseEntry.class);
        return cache.get(player.getPlayerId());
    }

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
