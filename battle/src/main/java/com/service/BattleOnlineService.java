package com.service;

import com.pojo.BattlePlayer;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BattleOnlineService extends BaseService{
	
	private ConcurrentHashMap<Long,BattlePlayer> battlePlayerMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, Long> userIdPlayerIdMap = new ConcurrentHashMap<>();
	
	
	public void putPlayer(BattlePlayer battlePlayer){
		battlePlayerMap.put(battlePlayer.getPlayerId(),battlePlayer);
	}
	
	public void putPlayer(long uid, BattlePlayer battlePlayer) {
		userIdPlayerIdMap.put(uid, battlePlayer.getPlayerId());
	}
	
	public BattlePlayer getPlayerByUid(long uid) {
		Long playerId = userIdPlayerIdMap.get(uid);
		if (Objects.isNull(playerId)) {
			return null;
		}
		return battlePlayerMap.get(playerId);
	}
	
	@Override
	public void onStart(){
	
	}
	
	@Override
	public void onClose(){
	
	}
}
