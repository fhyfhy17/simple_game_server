package com.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerInfo {
    private long playerId;
    private long uid;
    private long level;
    private long exp;
    private long coin;
    private long unionId;
}
