package com;

import com.util.IpUtil;

public class Main {
    public static void main(String[] args) {
        new NettyClient(IpUtil.getHostIp(), 23023);
    }
}
