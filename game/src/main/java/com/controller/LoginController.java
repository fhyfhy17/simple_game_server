package com.controller;

import com.annotation.Controllor;
import com.event.EventDispatcher;
import com.event.playerEvent.PlayerLoginEvent;
import com.net.msg.LOGIN_MSG;
import com.pojo.Player;
import com.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class LoginController extends BaseController {
    @Autowired
    private PlayerService playerService;

    @Controllor
    public LOGIN_MSG.GTC_PLAYER_LIST playerList(UidContext uidContext, LOGIN_MSG.CTG_PLAYER_LIST req) {
        LOGIN_MSG.GTC_PLAYER_LIST.Builder builder = LOGIN_MSG.GTC_PLAYER_LIST.newBuilder();

        playerService.playerList(uidContext.getUid(), builder);

        return builder.build();
    }

    @Controllor
    public LOGIN_MSG.GTC_GAME_LOGIN_PLAYER gameLogin(UidContext uidContext, LOGIN_MSG.CTG_GAME_LOGIN_PLAYER req) {
        LOGIN_MSG.GTC_GAME_LOGIN_PLAYER.Builder builder = LOGIN_MSG.GTC_GAME_LOGIN_PLAYER.newBuilder();
        EventDispatcher.playerEventDispatch(new PlayerLoginEvent(req.getPlayerId(), uidContext.getUid(), builder));
        return builder.build();
    }

    @Controllor
    public LOGIN_MSG.GTC_PlayerInfo getPlayerInfo(UidContext uidContext, Player player, LOGIN_MSG.CTG_PlayerInfo req) {
        LOGIN_MSG.GTC_PlayerInfo.Builder builder = LOGIN_MSG.GTC_PlayerInfo.newBuilder();

        builder.setPlayerInfo(playerService.buildPlayerInfo(player.playerPart.getPlayerEntry()));
        return builder.build();
    }


}
