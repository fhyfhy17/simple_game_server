package com.service;

import com.dao.UnionRepository;
import com.entry.UnionEntry;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@Data
//玩家信息Service
//TODO 这里打算做一个玩家信息展示的服务
// 但是启动时全搂库不太靠谱，打算做，在工会中的，排行榜的任何出现在公众位置的一个信息的展示，这里应该拼一个数据库搜索
// 如果是玩家好友，可以在玩家上线时，进行信息的加载，不用开服即加载
// 暂时先这样，也可以考虑一下redis的方案，比如另搞个信息服，或者在bus中
public class BusPlayerInfoService {

    private Map<Long, UnionEntry> unionMap = Maps.newHashMap();

    @Autowired
    private UnionRepository unionRepository;

    public void getUnionList() {

    }

    public boolean addToUnion(Long unionId) {
        return true;
    }

}
