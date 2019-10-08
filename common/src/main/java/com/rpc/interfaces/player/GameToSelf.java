package com.rpc.interfaces.player;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Rpc;
import com.entry.CenterMailEntry;

public interface GameToSelf {
    @Suspendable
    @Rpc(needResponse = false)
    default Object centerMail(Long playerId, CenterMailEntry centerMailEntry) {
        return null;
    }


}
