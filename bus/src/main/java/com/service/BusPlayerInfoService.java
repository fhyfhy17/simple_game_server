package com.service;

import com.entry.PlayerEntry;
import com.pojo.PlayerInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
//玩家信息Service
//TODO 这里打算做一个玩家信息展示的服务
// 但是启动时全搂库不太靠谱，打算做，在工会中的，排行榜的任何出现在公众位置的一个信息的展示，这里应该拼一个数据库搜索
// 如果是玩家好友，可以在玩家上线时，进行信息的加载，不用开服即加载
// 暂时先这样，也可以考虑一下redis的方案，比如另搞个信息服，或者在bus中
@Order(5)
public class BusPlayerInfoService extends BaseService {

    @Getter
    private Map<Long, PlayerInfo> playerInfoMap = new HashMap<>();

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UnionService unionService;


    @Override
    public void onStart() {
        //加载所有帮派中的角色信息
        for (PlayerEntry playerEntry : unionService.getAllUnionPlayer()) {
            playerInfoMap.put(playerEntry.getId(), new PlayerInfo(playerEntry.getId(), playerEntry.getUid()
                    , playerEntry.getLevel(), playerEntry.getExp(), playerEntry.getCoin(), playerEntry.getUnionId()));
        }
    }

    @Override
    public void onClose() {

    }

}
