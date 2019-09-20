package com.service;

import com.annotation.EventListener;
import com.pojo.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@EventListener
@Slf4j
public class UnionService extends BaseService {

    @Autowired
    private OnlineService onlineService;


    public boolean playerInUnion(long playerId){
        return onlineService.getPlayer(playerId).getUnionModule().hasUnion();
    }
    
    public long getPlayerUnionId(long playerId){
        if(!playerInUnion(playerId)){
            return 0;
        }
        return onlineService.getPlayer(playerId).getUnionModule().getUnionId();
    }

    public void createUnion(Player player, String unionName) throws Throwable {
        player.getUnionModule().createUnion(unionName);
    }
    
    @Override
    public void onStart() {

    }

    @Override
    public void onClose() {

    }
}
