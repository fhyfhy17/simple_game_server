package com.abs.impl;

import com.abs.NoCellBagAbs;
import com.pojo.Player;
import com.template.TemplateManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 有上限，且领取奖励时，超过上限不让领取的特殊无格背包
 */

@Slf4j
public class LimitNoCellBag extends NoCellBagAbs {


    public void init(Map<Integer, Long> map, TemplateManager templateManager, Player player) {
        super.init(map, templateManager, player);

    }


}
