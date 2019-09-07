package com.entry;

import com.entry.po.TaskPo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
public class TaskEntry extends BaseEntry {

    public TaskEntry(long id) {
        super(id);
    }

    private List<TaskPo> tasks = new ArrayList<>();
    @Indexed
    private long playerId;

}

