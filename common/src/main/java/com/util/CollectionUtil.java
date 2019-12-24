package com.util;

import java.util.*;
import java.util.stream.Collectors;

public class CollectionUtil {

    /**
     * 将一个List 分成N个子List， 每个子List里有 toIndex 个元素
     */
    public static <T> Map<Integer, List<T>> spiltList(List<T> oldList, int toIndex) {
        int listSize = oldList.size();
        Map<Integer, List<T>> map = new HashMap<>();
        int keyToken = 0;
        for (int i = 0; i < oldList.size(); i += toIndex) {
            if (i + toIndex > listSize) {
                toIndex = listSize - i;
            }
            List<T> newList = oldList.subList(i, i + toIndex);
            map.put(keyToken, newList);
            keyToken++;
        }

        return map;
    }

    /**
     * 通用 Map根据Value排序，注意返回的是LinkedHashMap
     *
     * @param map 需要排序的map
     * @param <K>
     * @param <V>
     * @return 有序的map
     */
    public static <K, V extends Comparable> LinkedHashMap<K, V> mapValueSort(Map<K, V> map) {
        return  map.entrySet().stream()
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

}
