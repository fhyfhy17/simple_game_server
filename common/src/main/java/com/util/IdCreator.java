package com.util;


import com.entry.BaseEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdCreator {

    private static Snowflake snowflake;

    @Autowired
    public void setSnowflake(Snowflake snowflake) {
        IdCreator.snowflake = snowflake;
    }

    public static long nextId(Class<? extends BaseEntry> clazz) {
        return IdCreator.snowflake.nextId(clazz);
    }
}
