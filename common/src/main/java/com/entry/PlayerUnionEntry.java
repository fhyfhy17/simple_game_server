package com.entry;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 个人公会
 */
@Document
@Getter
@Setter
@ToString
public class PlayerUnionEntry extends BaseEntry {

    public PlayerUnionEntry(long id) {
        super(id);
    }

    private long unionId;
    private String unionName;
    private int unionLevel;

}

