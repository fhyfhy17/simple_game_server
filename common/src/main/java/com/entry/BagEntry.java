package com.entry;

import com.annotation.SeqClassName;
import com.entry.po.ItemPo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
@Getter
@Setter
@SeqClassName(name = "seq.BagEntry")
@ToString
public class BagEntry extends BaseEntry {


    public Map<Integer, ItemPo> indexMap = new HashMap<>();

    public BagEntry(long id) {
        super(id);
    }

}
