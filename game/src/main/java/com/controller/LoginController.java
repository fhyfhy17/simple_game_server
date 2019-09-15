package com.controller;

import com.annotation.Controllor;
import com.controller.interceptor.HandlerExecutionChain;
import com.net.msg.LOGIN_MSG;
import com.pojo.Packet;
import com.pojo.Player;
import com.rpc.interfaces.gameToBus.GameToGame;
import com.service.PlayerService;
import com.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
public class LoginController extends BaseController implements GameToGame {
    @Autowired
    private PlayerService playerService;

    @Controllor
    public Object playerList(UidContext context, LOGIN_MSG.CTG_PLAYER_LIST req) {
        LOGIN_MSG.GTC_PLAYER_LIST.Builder builder = LOGIN_MSG.GTC_PLAYER_LIST.newBuilder();

        CompletableFuture<List<LOGIN_MSG.PLAYER_INFO>> result = playerService.playerList(context.getUid());
        result.whenCompleteAsync((list, throwable) -> {
            ExceptionUtil.doThrow(throwable);
            builder.addAllPlayers(list);
            HandlerExecutionChain.applyPostHandle(
                    new Packet(context.getUid(), context.getId(), null, context.getFrom(), context.getGate(), context.getRpc())
                    , builder.build());
        });

        return null;
    }

    @Controllor
    public Object gameLogin(UidContext context, LOGIN_MSG.CTG_GAME_LOGIN_PLAYER req) {
        LOGIN_MSG.GTC_GAME_LOGIN_PLAYER.Builder builder = LOGIN_MSG.GTC_GAME_LOGIN_PLAYER.newBuilder();
        CompletableFuture<LOGIN_MSG.PLAYER_INFO> result = playerService.login(req.getPlayerId());
        result.whenCompleteAsync((playerInfo, throwable) -> {
            ExceptionUtil.doThrow(throwable);
            builder.setPlayerInfo(playerInfo);
            HandlerExecutionChain.applyPostHandle(
                    new Packet(context.getUid(), context.getId(), null, context.getFrom(), context.getGate(), context.getRpc())
                    , builder.build());
        });
        return builder.build();
    }

    @Controllor
    public LOGIN_MSG.GTC_PlayerInfo getPlayerInfo(UidContext uidContext, Player player, LOGIN_MSG.CTG_PlayerInfo req) {
        LOGIN_MSG.GTC_PlayerInfo.Builder builder = LOGIN_MSG.GTC_PlayerInfo.newBuilder();

        builder.setPlayerInfo(playerService.buildPlayerInfo(player.getPlayerModule().getPlayerEntry()));
        return builder.build();
    }

    @Controllor
    @Override
    public Object self(String a) {
        log.info(a + "  ========");
        return null;
    }
}
