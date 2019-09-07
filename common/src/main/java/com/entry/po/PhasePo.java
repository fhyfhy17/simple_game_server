package com.entry.po;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PhasePo {
    private int phase;
    private Date startTime = new Date();
    private Date finishTime;

}
