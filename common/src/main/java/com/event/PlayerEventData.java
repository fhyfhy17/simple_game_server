package com.event;

import lombok.Data;

import java.util.Date;

@Data
public class PlayerEventData {

    public PlayerEventData(long playerId) {
        this.playerId = playerId;
    }

    private long playerId;

    private Date time = new Date();
}
