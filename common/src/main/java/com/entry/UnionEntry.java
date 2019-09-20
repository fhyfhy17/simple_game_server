package com.entry;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 公会
 */
@Document
@Getter
@Setter
@ToString
public class UnionEntry extends BaseEntry {

    public UnionEntry(long id) {
        super(id);
    }

    private long contribution;

    private List<Long> applyList = new ArrayList<>();

    private List<Long> playerList = new ArrayList<>();

    private Date createTime = new Date();

    private String name;

    private int level = 1;

    private int exp;
}

