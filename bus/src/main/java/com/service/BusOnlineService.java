package com.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.pojo.OnlineContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Data
public class BusOnlineService extends BaseService {
    //TODO bus的在线信息，要game发过来心跳控制,不用每条都发，可以把整个Online的id全发过来，因为Game层已经有
    // 心跳控制了那么可以每次是该服的，不存在的就删除。暂时先这样，这里应该容易出现BUG，以后观察

    private Map<Long, OnlineContext> onlineMap = Maps.newHashMap();
    private Multimap<String,OnlineContext> onlineMapByGame = ArrayListMultimap.create();

    public OnlineContext getOnlineContext(long uid) {
        return onlineMap.get(uid);
    }

    public void putOnlineContext(OnlineContext onlineContext) {
        onlineMap.put(onlineContext.getUid(), onlineContext);
        onlineMapByGame.put(onlineContext.getGame(),onlineContext);
    }

    public void delOnlineContext(long uid) {
        OnlineContext onlineContext = onlineMap.get(uid);

        if(onlineContext != null){
            String game = onlineContext.getGame();
            onlineMapByGame.get(game).remove(onlineContext);
        }
        onlineMap.remove(uid);

    }

    public void onHeart(String from , List<Long> uids){
        Collection<OnlineContext> onlineContexts = onlineMapByGame.get(from);


    }

    @Override
    public void onStart() {

    }

    @Override
    public void onClose() {

    }
}
