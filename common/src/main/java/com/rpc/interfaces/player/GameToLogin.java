package com.rpc.interfaces.player;

import com.annotation.Rpc;
import com.pojo.Tuple;

import java.util.concurrent.CompletableFuture;

public interface GameToLogin {
    @Rpc(needResponse = true)
    default Tuple<String, Throwable> testResponse(Long playerId) {
        return null;
    }

    @Rpc(needResponse = true)
    default CompletableFuture<String> test2(Long playerId) {
        return null;
    }
}
