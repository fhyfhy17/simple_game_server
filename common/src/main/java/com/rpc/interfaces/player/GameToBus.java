package com.rpc.interfaces.player;

import com.annotation.Rpc;
import com.entry.UnionEntry;
import com.pojo.OnlineContext;

import java.util.concurrent.CompletableFuture;

public interface GameToBus {

    @Rpc(needResponse = true)
    default CompletableFuture<UnionEntry> createUnion(Long playerId,String unionName) {
        return null;
    }


    @Rpc(needResponse = false)
    default Object putOnline(OnlineContext onlineContext) {
        return null;
    }

    @Rpc(needResponse = false)
    default Object offline(Long uid) {
        return null;
    }

}
