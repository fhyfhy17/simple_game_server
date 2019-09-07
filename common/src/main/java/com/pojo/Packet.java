package com.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Packet implements Serializable {

    private static final long serialVersionUID = 1123834342L;
    private long uid; // uid
    private int id; // 协议号
    private byte[] data; // 协议内容
    private String from;
    private String gate;
}
