package com.enums;

public enum CacheEnum {
    /***/
    PlayerEntryCache,
    /***/
    UserEntryCache,
    /***/
    UnionEntryCache,
    /***/
    TaskEntryCache,
    /***/
    BagEntryCache,
    /***/
    NoCellBagEntryCache,
    /***/
    MailEntryCache,
    ;

    public static CacheEnum getEnumByName(String name) {
        return CacheEnum.valueOf(name);
    }
}
