package com.rpc.interfaces.player;

import com.annotation.Rpc;

import java.util.concurrent.CompletableFuture;

public interface GameToLogin {
    @Rpc(needResponse = true)
    default CompletableFuture<String> testResponse(Long playerId) {
        return null;
    }
}
