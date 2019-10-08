package com.rpc.interfaces.player;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Rpc;
import com.pojo.Tuple;

public interface GameToLogin {
    @Suspendable
    @Rpc(needResponse = true)
    default Tuple<String, Throwable> testResponse(Long playerId) {
        return null;
    }
}
