package com.rpc.interfaces.player;

import com.annotation.Rpc;

public interface GameToGame {
    @Rpc(needResponse = false)
    default Object self(String a) {
        return null;
    }


}
