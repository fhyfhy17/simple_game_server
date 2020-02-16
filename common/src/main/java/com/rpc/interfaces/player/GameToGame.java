package com.rpc.interfaces.player;

import com.annotation.Rpc;
import com.entry.CenterMailEntry;

public interface GameToGame {
    @Rpc(needResponse = false)
    default Object self(String a) {
        return null;
    }

    @Rpc(needResponse = false)
    default Object centerMail(CenterMailEntry centerMailEntry) {
        return null;
    }

}
