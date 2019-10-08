package com.rpc.interfaces.player;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Rpc;

public interface GameToGame {
    @Suspendable
    @Rpc(needResponse = false)
    default Object self(String a) {
        return null;
    }


}
