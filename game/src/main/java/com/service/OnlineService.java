package com.service;

import com.annotation.EventListener;
import com.entry.BaseEntry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.module.BaseModule;
import com.pojo.Player;
import com.util.DBUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public void offline(long playerId){
        //TODO 丢弃掉所有消息
        Player remove = playerMap.remove(playerId);
        userIdPlayerIdMap.remove(remove.getUid());
        List<BaseEntry> list = Lists.newArrayList();
        for (BaseModule module : remove.getModules()) {
            list.add(module.getEntry());
        }
        DBUtil.forceEntrySave(list,true);
    }


    public void onServerStop() {
        for (Player player : playerMap.values()) {
            offline(player.getPlayerId());
        }
    }
}
