package com.entry;

import com.annotation.SeqClassName;
import com.entry.po.MailPo;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
@SeqClassName(name = "seq.MailEntry")
@ToString
public class MailEntry extends BaseEntry {

    private List<MailPo> mailList = Lists.newArrayList();
    private List<Long> hasCenterMailIds = Lists.newArrayList();


    public MailEntry(long id) {
        super(id);
    }

}
