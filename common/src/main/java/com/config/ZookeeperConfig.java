package com.config;


import com.Constant;
import com.manager.ServerInfoManager;
import com.pojo.ServerInfo;
import com.util.ContextUtil;
import com.util.StringUtil;
import com.util.Util;
import com.util.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.RetryForever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class ZookeeperConfig {

    @Autowired
    private ServerInfo serverInfo;
    
    @Bean("curator")
    public CuratorFramework getCurator() {
        RetryPolicy retryPolicy = new RetryForever(20);
        CuratorFramework curator = CuratorFrameworkFactory.builder()
                .connectString(StringUtil.getSplitePrefix(ContextUtil.zkIpPort,":"))
                .namespace("sgs")
                .sessionTimeoutMs(6000)
                .retryPolicy(retryPolicy).build();
        return curator;
    }


    @PostConstruct
    public void curatorFramework() throws Exception {


        CuratorFramework curator = getCurator();
        curator.start();

        //递规创建路径，用在第一次在系统中启动时创建路径
        curator.checkExists().creatingParentContainersIfNeeded().forPath(Constant.ZOOKEEPER_PATH);

        //加入路径监听
        final PathChildrenCache childrenCache = new PathChildrenCache(curator, Constant.ZOOKEEPER_PATH, true);
        try {
            childrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


        childrenCache.getListenable().addListener(
                (client, event) -> {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            ServerInfoManager.addServer(Util.transToServerInfo(event.getData().getPath()));
                            break;
                        case CHILD_REMOVED:
                            ServerInfoManager.removeServer(Util.transToServerInfo(event.getData().getPath()));
                            break;
                        default:
                            break;
                    }
                }
        );
        ZookeeperUtil.connectZookeeper(serverInfo);

    }
}