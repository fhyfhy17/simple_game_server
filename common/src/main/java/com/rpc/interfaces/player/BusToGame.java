package com.rpc.interfaces.player;

import com.annotation.Rpc;
import com.entry.CenterMailEntry;

public interface BusToGame {
    @Rpc(needResponse = false)
    default Object centerMail(CenterMailEntry centerMailEntry) {
        return null;
    }
}
