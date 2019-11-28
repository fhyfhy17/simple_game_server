package com.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

@Slf4j
public class SystemUtil {

    public static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        log.info("当前进程的标识为：{}", name);
        int index = name.indexOf("@");
        int pid = 0;
        if (index != -1) {
            pid = Integer.parseInt(name.substring(0, index));
            log.info("当前进程的PID为：{}", pid);
        }
        return pid;
    }
}
