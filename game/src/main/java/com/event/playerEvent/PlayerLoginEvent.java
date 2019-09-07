package com.event.playerEvent;

import com.event.PlayerEventData;
import com.net.msg.LOGIN_MSG;
import lombok.Data;

@Data
public class PlayerLoginEvent extends PlayerEventData {
    private long uid;
    private LOGIN_MSG.GTC_GAME_LOGIN_PLAYER.Builder builder;

    public PlayerLoginEvent(long playerId, long uid, LOGIN_MSG.GTC_GAME_LOGIN_PLAYER.Builder builder) {
        super(playerId);
        this.uid = uid;
        this.builder = builder;
    }
}
