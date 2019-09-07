package com.entry.po;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class MailPo {
    private long mailId;

    private int mailTemplateId;
    private boolean hasRead;
    private boolean hasReceived;

    private List<ItemInfo> itemList = Lists.newArrayList();
    private long mailTime; //接收mail时间

}
