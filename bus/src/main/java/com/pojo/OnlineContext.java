package com.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnlineContext {
    private long uid;
    private long playerId;
    private String gate;
    private String game;
}
