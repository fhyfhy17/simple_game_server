package com.entry;

import com.annotation.SeqClassName;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
@SeqClassName(name = "seq.UserEntry")
public class UserEntry extends BaseEntry {

    public UserEntry(long id) {
        super(id);
    }

    @Indexed
    private String userName;

    private String passWord;

    private List<Long> playerIds = Lists.newArrayList();
}
