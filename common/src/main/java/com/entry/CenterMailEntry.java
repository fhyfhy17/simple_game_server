package com.entry;

import com.annotation.SeqClassName;
import com.entry.po.ItemInfo;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
@SeqClassName(name = "seq.CenterMailEntry")
@ToString
public class CenterMailEntry extends BaseEntry {

    private int type;
    private int mailTemplateId;
    private List<Long> receiverId = Lists.newArrayList();
    private List<ItemInfo> itemList = Lists.newArrayList();
    private long mailStartTime;
    private long mailEndTime;


    public CenterMailEntry(long id) {
        super(id);
    }

}
