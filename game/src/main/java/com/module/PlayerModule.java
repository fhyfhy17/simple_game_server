package com.module;

import com.dao.PlayerRepository;
import com.entry.BaseEntry;
import com.entry.PlayerEntry;
import com.exception.StatusException;
import com.template.templates.type.TipType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Order(1)
public class PlayerModule extends BaseModule {


    private PlayerEntry playerEntry;

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void onLoad() {
        player.setPlayerModule(this);
        playerEntry = playerRepository.findById(player.getPlayerId()).orElseThrow(() -> new StatusException(TipType.NoPlayer));
    }

    @Override
    public BaseEntry getEntry() {
        return playerEntry;
    }

    @Override
    public void onLogin() {

    }
}
