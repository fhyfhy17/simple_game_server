package com.service;

import com.dao.UnionRepository;
import com.entry.PlayerEntry;
import com.entry.UnionEntry;
import com.exception.StatusException;
import com.google.common.collect.Maps;
import com.template.templates.type.TipType;
import com.util.IdCreator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
//工会service
//TODO  关于bus设计成多线程还是单线程还没有想好，单线程不用处理同步，多线程速度有优势，但速度的优势也不是很大，如果有访问别的线程数据
// 的情况发生，比较麻烦
@Order(1)
public class UnionService extends BaseService {

    @Getter
    private Map<Long, UnionEntry> unionMap = Maps.newHashMap();

    @Autowired
    private UnionRepository unionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void getUnionList() {

    }

    public boolean addToUnion(Long unionId) {
        return true;
    }

    //启动的加载都可以同步执行
    //TODO 要有各服务器准备了的标记
    // 准备做一个服务器各阶段ENUM ， 启动，准备中，准备完毕，关闭中，已关闭等
    // 玩家身上也应该有类似的状态， 切服中，online , offline等
    public void loadUnions() {
        List<UnionEntry> all = unionRepository.findAll();
        for (UnionEntry unionEntry : all) {
            unionMap.put(unionEntry.getId(), unionEntry);
        }
    }

    /**
     * 取得所有帮派的所有成员
     *
     * @return
     */
    public List<PlayerEntry> getAllUnionPlayer() {
        return unionMap.values().stream().map(unionEntry -> {
            List<Long> playerList = unionEntry.getPlayerList();
            Query query = new Query();
            query.addCriteria(Criteria.where("id").in(playerList));
            return mongoTemplate.find(query, PlayerEntry.class);
        }).flatMap(Collection::stream).collect(Collectors.toList());
    }


    public CompletableFuture<UnionEntry> createUnion(long playerId,String unionName) {
        CompletableFuture<UnionEntry> completableFuture = new CompletableFuture<>();
        try {
            for (UnionEntry unionEntry : unionMap.values()) {
                if (unionEntry.getName().equals(unionName)) {
                    completableFuture.completeExceptionally(new StatusException(TipType.UnionNameExist));
                    return completableFuture;
                }
            }

            UnionEntry unionEntry = new UnionEntry(IdCreator.nextId(UnionEntry.class));
            unionEntry.setLeaderId(playerId);
            unionEntry.setName(unionName);
            unionRepository.save(unionEntry);
            completableFuture.complete(unionEntry);
        } catch (Exception e) {
            completableFuture.completeExceptionally(e);
        }

        return completableFuture;
    }

    @Override
    public void onStart() {
        loadUnions();
    }

    @Override
    public void onClose() {

    }

}
