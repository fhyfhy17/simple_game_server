package com.rpc.interfaces.system;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Rpc;

public interface SystemBusToGame {

    @Suspendable
    @Rpc(needResponse = false)
    default Object sendChat(String content) {
        return null;
    }
}
