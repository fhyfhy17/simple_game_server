package com.util;

import com.net.msg.COMMON_MSG;

public class TipStatus {

    public static COMMON_MSG.Status create(int tip, boolean result) {
        COMMON_MSG.Status.Builder builder = COMMON_MSG.Status.newBuilder();
        builder.setResult(result);
        builder.setTip(tip);
        return builder.build();
    }


    public static COMMON_MSG.Status suc() {
        COMMON_MSG.Status.Builder builder = COMMON_MSG.Status.newBuilder();
        builder.setResult(true);
        builder.setTip(0);
        return builder.build();
    }


    public static COMMON_MSG.Status fail(int tip) {
        COMMON_MSG.Status.Builder builder = COMMON_MSG.Status.newBuilder();
        builder.setResult(false);
        builder.setTip(tip);
        return builder.build();
    }

}
