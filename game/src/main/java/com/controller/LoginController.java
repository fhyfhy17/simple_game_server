package com.controller;

import com.annotation.Controllor;
import com.net.msg.LOGIN_MSG;
import com.pojo.OnlineContext;
import com.pojo.Param;
import com.pojo.Player;
import com.rpc.interfaces.player.GameToBus;
import com.rpc.interfaces.player.GameToGame;
import com.service.PlayerService;
import com.util.ContextUtil;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class LoginController extends BaseController implements GameToGame, GameToBus {
    @Autowired
    private PlayerService playerService;

    @Autowired
    @Qualifier("callBackForMessageThread")
    private Executor callBackForMessageThread;

    @Controllor
    public CompletableFuture<LOGIN_MSG.GTC_PLAYER_LIST> playerList(UidContext context, LOGIN_MSG.CTG_PLAYER_LIST req) {
        LOGIN_MSG.GTC_PLAYER_LIST.Builder builder = LOGIN_MSG.GTC_PLAYER_LIST.newBuilder();

        CompletableFuture<List<LOGIN_MSG.PLAYER_INFO>> result = playerService.playerList(context.getUid());
        return result.thenApply((list) ->{
            builder.addAllPlayers(list);
            return builder.build();
        });

    }

    @Controllor
    public CompletableFuture<LOGIN_MSG.GTC_GAME_LOGIN_PLAYER> gameLogin(UidContext context, LOGIN_MSG.CTG_GAME_LOGIN_PLAYER req) {
        LOGIN_MSG.GTC_GAME_LOGIN_PLAYER.Builder builder = LOGIN_MSG.GTC_GAME_LOGIN_PLAYER.newBuilder();
        CompletableFuture<LOGIN_MSG.PLAYER_INFO> result = playerService.login(req.getPlayerId());
		    Param param=new Param();
        return result.thenApplyAsync((playerInfo) -> {
            builder.setPlayerInfo(playerInfo);
            //异步的话，这还是IO线程 ----通过指定线程池加上ThreadLocal ，直接线程就切回了调用线程
            log.info(Thread.currentThread().getName() + "执行了一下");
            // 通知bus登陆信息
            putOnline(new OnlineContext(playerInfo.getUid(), playerInfo.getPlayerId(), context.getGate(), ContextUtil.id));

            log.info("param : {}" ,param);
			      log.info(Thread.currentThread().getName() + "   回调结束后执行了一下");
            return builder.build();
        }, callBackForMessageThread);
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
