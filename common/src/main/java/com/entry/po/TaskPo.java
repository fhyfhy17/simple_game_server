package com.entry.po;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TaskPo {
    private long taskId;
    private int type;
    private int status;
    private int currNum;

    private Date startTime = new Date();
    private Date statusTime = new Date();

    private List<PhasePo> phaseList = new ArrayList<>();
}
