package com.abs;

import com.entry.po.ItemInfo;
import com.google.common.collect.Lists;
import com.pojo.Player;
import com.template.TemplateManager;
import com.template.templates.ItemTemplate;
import com.template.templates.type.ItemUseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 无格背包接口，可做货币，数值化东西，也可做无格背包
 */

@Slf4j
public abstract class NoCellBagAbs {

    private TemplateManager tm;

    public Map<Integer, Long> map = new HashMap<>();

    private Player player;

    public void init(Map<Integer, Long> map, TemplateManager templateManager, Player player) {
        this.tm = templateManager;
        this.map = map;
        this.player = player;

    }


    public boolean putItems(List<ItemInfo> list) {

        for (ItemInfo itemInfo : list) {
            map.put(itemInfo.id, (long) itemInfo.num);
        }
        return true;
    }


    /**
     * 添加物品，放不下返回false
     *
     * @param itemInfos 物品s
     * @return 放不下返回false
     */
    public boolean addItem(List<ItemInfo> itemInfos) {
        return putItems(itemInfos);
    }

    public boolean addItem(Map<Integer, Integer> map) {
        return addItem(map.entrySet().stream().map(x -> new ItemInfo(x.getKey(), x.getValue())).collect(Collectors.toList()));
    }


    public boolean addItem(Integer itemId, Integer num) {
        return addItem(Lists.newArrayList(new ItemInfo(itemId, num)));
    }

    public boolean addItem(ItemInfo... itemInfos) {
        return addItem(Arrays.asList(itemInfos));

    }


    public boolean costItems(List<ItemInfo> list) {
        for (ItemInfo itemInfo : list) {
            if (!map.containsKey(itemInfo.id)) {
                return false;
            }
            long aLong = map.get(itemInfo.id);
            if (aLong < itemInfo.num) {
                return false;
            }
            long value = aLong - itemInfo.num;
            map.put(itemInfo.id, value);
        }
        return true;
    }


    public boolean costItems(Map<Integer, Integer> map) {
        return costItems(map.entrySet().stream().map(x -> new ItemInfo(x.getKey(), x.getValue())).collect(Collectors.toList()));
    }


    public boolean costItems(Integer itemId, Integer num) {
        return costItems(Lists.newArrayList(new ItemInfo(itemId, num)));
    }

    public boolean costItems(ItemInfo... itemInfos) {
        return costItems(Arrays.asList(itemInfos));

    }

    /**
     * 使用物品
     *
     * @param itemId 物品ID
     * @param num    个数
     * @return 使用成功
     */
    public boolean useItem(int itemId, int num) {
        if (!costItems(itemId, num)) {
            return false;
        }

        //TODO  使用条件等
        ItemTemplate t = tm.getTemplate(ItemTemplate.class, itemId);
        switch (t.getType()) {
            case ItemUseType.OpenBox:

                break;
            case ItemUseType.Cost:

                break;
            default:
                log.error("未配置使用类型");
                break;
        }
        return true;

    }


    @Data
    @AllArgsConstructor
    public static class TempCell {
        private long tempItemId;
        private int tempIndex;
        private int tempNum;
    }

}
