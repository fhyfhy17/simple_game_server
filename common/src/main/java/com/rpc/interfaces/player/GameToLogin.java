package com.rpc.interfaces.player;

import com.annotation.Rpc;
import com.pojo.Tuple;

public interface GameToLogin {
    @Rpc(needResponse = true)
    default Tuple<String, Throwable> testResponse(Long playerId) {
        return null;
    }
}
