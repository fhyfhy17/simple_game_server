package com.util;

import com.alibaba.fastjson.JSON;
import com.entry.po.ItemInfo;
import com.google.common.collect.Lists;
import com.net.msg.COMMON_MSG;
import com.pojo.ServerInfo;

import java.util.List;
import java.util.stream.Collectors;

public class GameUtil {
    public static ServerInfo transToServerInfo(String serverString) {
        ServerInfo serverInfo = JSON.parseObject(serverString.split("==")[1], ServerInfo.class);
        return serverInfo;
    }

    public static List<ItemInfo> createItemInfoList(List<List<Integer>> list) {
        List<ItemInfo> itemInfos = Lists.newArrayList();
        for (List<Integer> integers : list) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setId(integers.get(0));
            itemInfo.setNum(integers.get(1));
            itemInfos.add(itemInfo);
        }
        return itemInfos;
    }

    public static List<COMMON_MSG.ItemInfo> transferItemInfoList(List<ItemInfo> list) {
        return list.stream().map(x -> {
            COMMON_MSG.ItemInfo.Builder itemInfo = COMMON_MSG.ItemInfo.newBuilder();
            itemInfo.setItemId(x.getId());
            itemInfo.setItemNum(x.getNum());
            return itemInfo.build();
        }).collect(Collectors.toList());
    }

}
