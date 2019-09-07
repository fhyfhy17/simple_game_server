package com.module;

import com.entry.PlayerEntry;
import lombok.Getter;
import lombok.Setter;
import org.ehcache.Cache;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Getter
@Setter
@Order(1)
public class PlayerModule extends BaseModule
{


    private PlayerEntry playerEntry;

    @Override
    public void onLoad() {
        player.playerPart = this;
        Cache<Long, PlayerEntry> cache = cacheManager.getCache(getCacheName(), Long.class, PlayerEntry.class);
        playerEntry = cache.get(player.getPlayerId());
        if (Objects.isNull(playerEntry)) {
            playerEntry = new PlayerEntry(player.getPlayerId());
            cache.put(player.getPlayerId(), playerEntry);
        }
    }


    @Override
    public void onLogin() {

    }
}
