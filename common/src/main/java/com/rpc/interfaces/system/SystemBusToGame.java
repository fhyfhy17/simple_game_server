package com.rpc.interfaces.system;

import com.annotation.Rpc;

public interface SystemBusToGame {

    @Rpc(needResponse = false)
    default Object sendChat(String content) {
        return null;
    }
}
