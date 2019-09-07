package com.module;

import com.abs.CellBagAbs;
import com.abs.impl.CommonCellBag;
import com.entry.BagEntry;
import com.template.TemplateManager;
import lombok.Getter;
import lombok.Setter;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Getter
@Setter
@Order(2)
public class BagModule extends BaseModule
{

    private BagEntry bagEntry;
    @Autowired
    private TemplateManager templateManager;

    private CellBagAbs bag;

    @Override
    public void onLoad() {
        player.bagPart = this;
        Cache<Long, BagEntry> cache = cacheManager.getCache(getCacheName(), Long.class, BagEntry.class);
        bagEntry = cache.get(player.getPlayerId());
        if (Objects.isNull(bagEntry)) {
            bagEntry = new BagEntry(player.getPlayerId());
            cache.put(player.getPlayerId(), bagEntry);
        }
        bag = new CommonCellBag();
        bag.init(bagEntry.indexMap, templateManager, player);

    }


    @Override
    public void onLogin() {

    }
}
