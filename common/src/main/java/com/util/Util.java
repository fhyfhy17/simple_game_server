package com.util;

import com.alibaba.fastjson.JSON;
import com.entry.po.ItemInfo;
import com.google.common.collect.Lists;
import com.net.msg.COMMON_MSG;
import com.pojo.ServerInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName Util
 * @Description 通用工具类
 * @Author dafeng
 * @Date 2019/1/28 11:29
 **/
public class Util {
    /**
     * 通用 Map根据Value排序，注意返回的是LinkedHashMap
     *
     * @param map 需要排序的map
     * @param <K>
     * @param <V>
     * @return 有序的map
     */
    public static <K, V extends Comparable> LinkedHashMap<K, V> mapValueSort(Map<K, V> map) {
        return (LinkedHashMap<K, V>) map.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .collect(Collectors.toMap((Map.Entry<K, V> k) -> k.getKey(), (Map.Entry<K, V> v) -> v.getValue(), (k, v) -> v, LinkedHashMap::new));

    }


    /**
     * 通用 Map根据Value排序，返回值的List
     *
     * @param map 需要排序的map
     * @param <K>
     * @param <V>
     * @return value的List
     */
    public static <K, V extends Comparable> List<V> mapValueSortReturnList(Map<K, V> map) {
        List<V> collect = map.values().stream().sorted().collect(Collectors.toList());
        return collect;
    }

    /**
     * 数组转List
     *
     * @param us  要操作的数组
     * @param <T>
     * @return List
     */
    public static <T> List<T> arrayToList(T[] us) {
        return Arrays.stream(us).collect(Collectors.toList());
    }


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
