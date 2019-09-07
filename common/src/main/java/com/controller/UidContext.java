package com.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UidContext {
    private long uid;
    private String from;
    private String gate;
}
