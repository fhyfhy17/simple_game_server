package com.service;

import com.Constant;
import com.alibaba.fastjson.JSONObject;
import com.annotation.EventListener;
import com.annotation.ServiceLog;
import com.aop.ServiceLogAspect;
import com.dao.PlayerRepository;
import com.dao.UserRepository;
import com.entry.PlayerEntry;
import com.entry.UserEntry;
import com.exception.StatusException;
import com.google.common.collect.Lists;
import com.module.BaseModule;
import com.net.msg.LOGIN_MSG;
import com.pojo.Player;
import com.template.templates.type.TipType;
import com.util.IdCreator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@EventListener
@Slf4j
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OnlineService onlineService;

    @Autowired
    private List<BaseModule> modules;


    @Async(Constant.IO_THREAD_NAME)
    @ServiceLog
    public CompletableFuture<LOGIN_MSG.PLAYER_INFO> login(long playerId) throws StatusException {
        Player player = loadPlayer(playerId);
        onlineService.putPlayer(player.getUid(), player);
        onlineService.putPlayer(player);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("player", player);
        ServiceLogAspect.THREAD_LOCAL.set(jsonObject);
        return CompletableFuture.completedFuture(buildPlayerInfo(player.getPlayerModule().getPlayerEntry()).build());
    }

    private Player loadPlayer(long playerId) {
        Player player = new Player();
        player.setPlayerId(playerId);
        player.initParts(modules);
        player.setPlayerId(playerId);
        player.setUid(player.getPlayerModule().getPlayerEntry().getUid());
        player.initParts(modules);


        return player;

    }

    @Async(Constant.IO_THREAD_NAME)
    public CompletableFuture<List<LOGIN_MSG.PLAYER_INFO>> playerList(long uid) {
        UserEntry user = userRepository.findById(uid).orElseThrow(() -> new StatusException(TipType.NoUid));
        List<Long> playerIds = user.getPlayerIds();
        if (CollectionUtils.isEmpty(playerIds)) {
            // 创建一个角色并存储
            long playerId = IdCreator.nextId(PlayerEntry.class);
            PlayerEntry playerEntry = new PlayerEntry(playerId);
            playerEntry.setName("游客" + playerId);
            playerEntry.setUid(uid);
            // 存储到角色列表
            user.getPlayerIds().add(playerId);
            userRepository.save(user);
            playerIds = user.getPlayerIds();
        }
        List<LOGIN_MSG.PLAYER_INFO> list = Lists.newArrayList();
        for (Long playerId : playerIds) {
            PlayerEntry playerEntry = playerRepository.findById(playerId).orElseThrow(() -> new StatusException(TipType.NoPlayer));
            list.add(buildPlayerInfo(playerEntry).build());
        }
        return CompletableFuture.completedFuture(list);
    }

    public LOGIN_MSG.PLAYER_INFO.Builder buildPlayerInfo(PlayerEntry playerEntry) {
        LOGIN_MSG.PLAYER_INFO.Builder playerBuilder = LOGIN_MSG.PLAYER_INFO.newBuilder();
        playerBuilder.setUid(playerEntry.getUid());
        playerBuilder.setPlayerId(playerEntry.getId());
        playerBuilder.setName(playerEntry.getName());
        playerBuilder.setLevel(playerEntry.getLevel());
        playerBuilder.setCoin(playerEntry.getCoin());
        return playerBuilder;
    }
}
