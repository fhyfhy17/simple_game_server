package com.entry;

import com.annotation.SeqClassName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@SeqClassName(name = "seq.PlayerEntry")
@ToString
public class PlayerEntry extends BaseEntry {


    public PlayerEntry(long id) {
        super(id);
    }

    private long uid;

    @Indexed
    private String name;

    private int level = 1;
    private long exp;
    private long coin;
    private long unionId;
}
