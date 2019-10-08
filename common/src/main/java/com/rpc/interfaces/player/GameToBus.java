package com.rpc.interfaces.player;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Rpc;
import com.entry.UnionEntry;
import com.pojo.OnlineContext;
import com.pojo.Tuple;

public interface GameToBus {

    @Suspendable
    @Rpc(needResponse = true)
    default Tuple<UnionEntry, Throwable> createUnion(Long playerId, String unionName) {
        return null;
    }


    @Suspendable
    @Rpc(needResponse = false)
    default Object putOnline(OnlineContext onlineContext) {
        return null;
    }

    @Suspendable
    @Rpc(needResponse = false)
    default Object offline(Long uid) {
        return null;
    }

}
