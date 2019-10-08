package com.controller;

import com.annotation.Controllor;
import com.handler.ContextHolder;
import com.net.msg.LOGIN_MSG;
import com.pojo.OnlineContext;
import com.pojo.Player;
import com.rpc.interfaces.player.GameToBus;
import com.rpc.interfaces.player.GameToGame;
import com.service.PlayerService;
import com.thread.schedule.ScheduleTask;
import com.util.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
public class LoginController extends BaseController implements GameToGame, GameToBus {
    @Autowired
    private PlayerService playerService;

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
        return result.thenApply((playerInfo)->{
            builder.setPlayerInfo(playerInfo);
            //异步的话，这还是IO线程
            log.info(Thread.currentThread().getName() + "执行了一下");
            // 通知bus登陆信息
            putOnline(new OnlineContext(playerInfo.getUid(), playerInfo.getPlayerId(), context.getGate(), ContextUtil.id));
            //这是调回通信线程
            ContextHolder.getScheduleAble().schedulePeriod(new ScheduleTask() {
                @Override
                public void execute() {
                    log.info(Thread.currentThread().getName() + "   12345");
                    //throw new StatusException(3);
                }
            }, 4, 500);
            
            return builder.build();
        });
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
