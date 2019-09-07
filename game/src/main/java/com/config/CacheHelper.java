package com.config;

import com.entry.PlayerEntry;
import com.entry.UnionEntry;
import com.entry.UserEntry;
import com.enums.CacheEnum;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CacheHelper {


    private static CacheManager cacheManager;

    public static Cache<Long, PlayerEntry> getPlayerEntryCache() {
        return cacheManager.getCache(CacheEnum.PlayerEntryCache.name(), Long.class, PlayerEntry.class);
    }

    public static Cache<Long, UserEntry> getUserEntryCache() {
        return cacheManager.getCache(CacheEnum.UserEntryCache.name(), Long.class, UserEntry.class);
    }

    public static Cache<Long, UnionEntry> getUnionEntryCache() {
        return cacheManager.getCache(CacheEnum.UnionEntryCache.name(), Long.class, UnionEntry.class);
    }


    @Autowired

    @Qualifier("cacheManagerMy")
    public void setCacheManager(CacheManager cacheManager) {
        CacheHelper.cacheManager = cacheManager;
    }

}
