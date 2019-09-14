package com.service;

import com.dao.UnionRepository;
import com.entry.UnionEntry;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Data
//工会service
//TODO  关于bus设计成多线程还是单线程还没有想好，单线程不用处理同步，多线程速度有优势，但速度的优势也不是很大，如果有访问别的线程数据
// 的情况发生，比较麻烦
public class UnionService {

    private Map<Long, UnionEntry> unionMap = Maps.newHashMap();

    @Autowired
    private UnionRepository unionRepository;

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
}
