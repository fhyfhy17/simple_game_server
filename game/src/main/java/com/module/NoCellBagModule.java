package com.module;

import com.abs.NoCellBagAbs;
import com.abs.impl.CommonNoCellBag;
import com.entry.NoCellBagEntry;
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
public class NoCellBagModule extends BaseModule
{

    private NoCellBagEntry noCellBagEntry;
    @Autowired
    private TemplateManager templateManager;

    private NoCellBagAbs noCellBag;

    @Override
    public void onLoad() {
        player.noCellBagPart = this;
        Cache<Long, NoCellBagEntry> cache = cacheManager.getCache(getCacheName(), Long.class, NoCellBagEntry.class);
        noCellBagEntry = cache.get(player.getPlayerId());
        if (Objects.isNull(noCellBagEntry)) {
            noCellBagEntry = new NoCellBagEntry(player.getPlayerId());
            cache.put(player.getPlayerId(), noCellBagEntry);
        }
        noCellBag = new CommonNoCellBag();
        noCellBag.init(noCellBagEntry.map, templateManager, player);

    }


    @Override
    public void onLogin() {

    }
}
