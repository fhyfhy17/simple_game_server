package com.entry;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "_sequence")
@Getter
@Setter
public class SeqEntry implements Serialize {


    private String collName;// 集合名称


    private long seqId;// 序列值


}