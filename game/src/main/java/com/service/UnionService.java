package com.service;

import com.annotation.EventListener;
import com.annotation.IgniteTransaction;
import com.annotation.P;
import com.dao.UnionRepository;
import com.enums.CacheEnum;
import com.enums.CacheParamterEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@EventListener
@Slf4j
public class UnionService {
    @Autowired
    private UnionRepository unionRepository;


    @IgniteTransaction(cacheEnum = {CacheEnum.UnionEntryCache})
    public void addContribute(@P(p = CacheParamterEnum.UnionEntryCache) long unionId, long contribute) {

//        UnionEntry unionEntry = (UnionEntry) IgniteTransactionAspect.THREAD_LOCAL.get()[0];
//        unionEntry.setContribution(unionEntry.getContribution() + contribute);

    }

    @IgniteTransaction(cacheEnum = {CacheEnum.UnionEntryCache, CacheEnum.PlayerEntryCache})
    public void examinePlayer(
            @P(p = CacheParamterEnum.UnionEntryCache) long unionId
            , @P(p = CacheParamterEnum.PlayerEntryCache) long playerId) {
//        UnionEntry unionEntry = (UnionEntry) IgniteTransactionAspect.THREAD_LOCAL.get()[0];
//        PlayerEntry playerEntry = (PlayerEntry) IgniteTransactionAspect.THREAD_LOCAL.get()[1];
//        unionEntry.getApplyList().remove(playerId);
//        unionEntry.getPlayerList().add(playerId);
//        playerEntry.setUnionId(unionId);

    }

}
