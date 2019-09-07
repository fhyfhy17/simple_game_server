package com.event.playerEvent;

import com.event.PlayerEventData;
import lombok.Data;

@Data
public class TestEvent extends PlayerEventData {
    private String testWord;

    public TestEvent(long playerId, String testWord) {
        super(playerId);
        this.testWord = testWord;
    }
}
