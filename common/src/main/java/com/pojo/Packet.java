package com.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Packet implements Serializable {

    private static final long serialVersionUID = 1123834342L;
    private long uid; // uid
    private int id; // 协议号
    private byte[] data; // 协议内容
    private String from;
    private String gate;
    private String rpc;//请求时为RPC请求ID 前缀为 rpcRequest，返回时为固定字符串"rpcResponse",为""不是RPC
}
