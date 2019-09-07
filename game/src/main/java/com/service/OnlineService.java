package com.service;

import com.annotation.EventListener;
import com.google.common.collect.Maps;
import com.pojo.Player;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@EventListener
@Slf4j
@Data
public class OnlineService {
    private Map<Long, Player> playerMap = Maps.newHashMap();
    private Map<Long, Long> userIdPlayerIdMap = Maps.newHashMap();

    public Player getPlayer(long playerId) {
        return playerMap.get(playerId);
    }

    public void putPlayer(Player player) {
        playerMap.put(player.getPlayerId(), player);
    }

    public void putPlayer(long uid, Player player) {
        userIdPlayerIdMap.put(uid, player.getPlayerId());
    }

    public Player getPlayerByUid(long uid) {
        Long playerId = userIdPlayerIdMap.get(uid);
        if (Objects.isNull(playerId)) {
            return null;
        }
        return playerMap.get(playerId);
    }
}
