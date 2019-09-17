package com.module;

import com.abs.CellBagAbs;
import com.abs.impl.CommonCellBag;
import com.dao.BagRepository;
import com.entry.BagEntry;
import com.entry.BaseEntry;
import com.template.TemplateManager;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

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
    @Autowired
    private BagRepository bagRepository;

    @Override
    public void onLoad() {
        player.setBagModule(this);
        bagEntry = bagRepository.findById(player.getPlayerId()).orElse(new BagEntry(player.getPlayerId()));
        bag = new CommonCellBag();
        bag.init(bagEntry.indexMap, templateManager, player);

    }

    @Override
    public BaseEntry getEntry() {
        return bagEntry;
    }
    
    @Override
    public CrudRepository getRepository(){
        return bagRepository;
    }
    
    
    @Override
    public void onLogin() {

    }
}
