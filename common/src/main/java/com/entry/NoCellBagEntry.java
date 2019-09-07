package com.entry;

import com.annotation.SeqClassName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
@Getter
@Setter
@SeqClassName(name = "seq.NoCellBagEntry")
@ToString
public class NoCellBagEntry extends BaseEntry {

    public Map<Integer, Long> map = new HashMap<>();

    public NoCellBagEntry(long id) {
        super(id);
    }

}
