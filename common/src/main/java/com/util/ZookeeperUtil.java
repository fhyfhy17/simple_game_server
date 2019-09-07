package com.util;

import com.Constant;
import com.alibaba.fastjson.JSON;
import com.pojo.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.util.Objects;

@Slf4j
public class ZookeeperUtil {

    public static void connectZookeeper(ServerInfo serverInfo) {

        CuratorFramework curator = SpringUtils.getBean("curator");
        if (Objects.isNull(curator)) {
            log.error("curator 为空");
            return;
        }
        //创建临时节点
        String uuid = ContextUtil.id + "==" + JSON.toJSONString(serverInfo);
        try {
            curator.create().withMode(CreateMode.EPHEMERAL).forPath(Constant.ZOOKEEPER_PATH + "/" + uuid, uuid.getBytes());
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
