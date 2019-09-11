package com.service;

import com.annotation.EventListener;
import com.dao.cache.CacheCenter;
import com.entry.BaseEntry;
import com.entry.PlayerEntryMark;
import com.google.common.collect.Maps;
import com.module.BaseModule;
import com.pojo.Player;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@EventListener
@Slf4j
@Data
public class DbService
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CacheCenter cacheCenter;
    
    public void forcePlayerEntrySave(List<PlayerEntryMark> list ){
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        long playerId = ((BaseEntry)list.get(0)).getId();
        synchronized(cacheCenter.lock){
            cacheCenter.clearWhenDelete(playerId);
        }
        for(PlayerEntryMark playerEntryMark : list){
            mongoTemplate.save(playerEntryMark);
        }
    }
    
    public void forceSave(List<BaseEntry> list ){
        if(CollectionUtils.isEmpty(list)){
            return;
        }
    }
}
