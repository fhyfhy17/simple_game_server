package com.entry.po;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemInfo {

    /**
     * 物品ID
     */
    public int id;

    /**
     * 物品数目
     */
    public int num;


    public ItemInfo() {
    }

    public ItemInfo(int id, int num) {
        this.num = num;
        this.id = id;
    }

}
