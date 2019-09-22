package com.service;

import com.annotation.EventListener;
import com.entry.BaseEntry;
import com.enums.TypeEnum;
import com.google.common.collect.Lists;
import com.module.BaseModule;
import com.pojo.Player;
import com.util.DBUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EventListener
@Slf4j
@Data
public class OnlineService extends BaseService {
    private ConcurrentHashMap<Long, Player> playerMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Long> userIdPlayerIdMap = new ConcurrentHashMap<>();

    public Player getPlayer(long playerId) {
        return playerMap.get(playerId);
    }

    public void putPlayer(Player player) {
        //TODO  player的状态，这里offline只是删除，添加，将来有切服，下线操作时可以做的细致点
        playerMap.put(player.getPlayerId(), player);
        player.setPlayerStatus(TypeEnum.PlayerStatus.ONLINE);
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
    

    @Override
    public void onStart() {

    }

    @Override
    public void onClose() {
        for(Player player : playerMap.values()){
            for(BaseModule module : player.getModules()){
                module.getRepository().save(module.getEntry());
            }
        }
        playerMap.clear();
        userIdPlayerIdMap.clear();
    }
}
