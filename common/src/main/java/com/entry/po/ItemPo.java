package com.entry.po;

import com.template.templates.ItemTemplate;
import com.util.SpringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemPo implements Comparable<ItemPo> {

    /**
     * 物品ID
     */
    public int id;

    /**
     * 物品数目
     */
    public int num;
    /**
     * 所在序号
     */
    public int index = -1;
    /**
     * 失效时间(-1为失效或过期)
     */
    public long disableTime = -1L;

    /**
     * 是否绑定
     */
    public boolean isBind;

    /**
     * 是否有红点
     */
    public boolean hasRedPoint;


    public ItemPo() {
    }


    /**
     * 是否达到单个数目上限
     */
    public boolean isSingleNumMax() {
        //FIXME 老是这么调用也不对，看看怎么搞
        ItemTemplate config = SpringUtils.getBean(ItemTemplate.class);
        return config.getSinglePlusMax() > 0 && num >= config.getSinglePlusMax();
    }

    /**
     * 获取单个叠加空数(-1为无限)
     */
    public int getPlusLast() {
        ItemTemplate config = SpringUtils.getBean(ItemTemplate.class);
        if (config.getSinglePlusMax() <= 0)
            return -1;

        if (num >= config.getSinglePlusMax())
            return 0;

        return config.getSinglePlusMax() - num;
    }

    //TODO 这需要具体排序规则
    @Override
    public int compareTo(ItemPo o) {
        return 0;
    }
}
