package com.abs.impl;

import com.abs.CellBagAbs;
import com.entry.po.ItemPo;
import com.pojo.Player;
import com.template.TemplateManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 有格背包，可做背包 ，仓库，物品栏等
 */

@Slf4j
public class CommonCellBag extends CellBagAbs {


    public void init(Map<Integer, ItemPo> indexMap, TemplateManager templateManager, Player player) {
        super.init(indexMap, templateManager, player);
    }


}
