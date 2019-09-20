package com.service;

import com.annotation.EventListener;
import com.entry.CenterMailEntry;
import com.pojo.Player;
import com.template.templates.type.CenterMailType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
    
	/**
	 * 世界邮件的接收处理
     */
    public void onCenterMail(CenterMailEntry centerMailEntry){
        Map<Long,Player> playerMap=onlineService.getPlayerMap();
        switch(centerMailEntry.getType()){
            case CenterMailType.Personal:
                if(playerMap.containsKey(centerMailEntry.getReceiverId().iterator().next())){
                    playerMap.get(centerMailEntry.getReceiverId().iterator().next()).getMailModule().onCenterMail(centerMailEntry);
                }
                break;
            case CenterMailType.Multiple:
                for(Long playerId : centerMailEntry.getReceiverId()){
                    if(onlineService.getPlayerMap().containsKey(playerId)){
                        Player player=onlineService.getPlayerMap().get(playerId);
                        player.getMailModule().onCenterMail(centerMailEntry);
                    }
                }
                break;
            case CenterMailType.Total:
                for(Player player : playerMap.values()){
                    player.getMailModule().onCenterMail(centerMailEntry);
                }
                
                break;
            default:
                log.info("类型错误 世界邮件 centermail = {}",centerMailEntry);
        }
    }
    
    
    @Override
    public void onStart() {

    }

    @Override
    public void onClose() {

    }
}
