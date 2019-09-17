package com.module;

import com.abs.NoCellBagAbs;
import com.abs.impl.CommonNoCellBag;
import com.dao.NoCellBagRepository;
import com.entry.BaseEntry;
import com.entry.NoCellBagEntry;
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
public class NoCellBagModule extends BaseModule{

    private NoCellBagEntry noCellBagEntry;
    @Autowired
    private TemplateManager templateManager;

    private NoCellBagAbs noCellBag;

    @Autowired
    private NoCellBagRepository noCellBagRepository;
    @Override
    public void onLoad() {
        player.setNoCellBagModule(this);
        noCellBagEntry = noCellBagRepository.findById(player.getPlayerId()).orElse(new NoCellBagEntry(player.getPlayerId()));
        noCellBag = new CommonNoCellBag();
        noCellBag.init(noCellBagEntry.map, templateManager, player);

    }

    @Override
    public BaseEntry getEntry() {
        return noCellBagEntry;
    }
    
    @Override
    public CrudRepository getRepository(){
        return noCellBagRepository;
    }
    
    @Override
    public void onLogin() {

    }
}
