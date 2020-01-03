package com.config;


import com.Constant;
import com.manager.ServerInfoManager;
import com.manager.ServerManager;
import com.pojo.ServerInfo;
import com.service.BaseService;
import com.util.ContextUtil;
import com.util.GameUtil;
import com.util.StringUtil;
import com.util.ZookeeperUtil;
import com.util.support.Cat;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.RetryForever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ZookeeperConfig extends BaseService {

    @Autowired
    private ServerInfo serverInfo;

    private CuratorFramework curator;
    private PathChildrenCache childrenCache;

    @Bean("curator")
    public CuratorFramework getCurator() {
        RetryPolicy retryPolicy = new RetryForever(200);
        CuratorFramework curator = CuratorFrameworkFactory.builder()
                .connectString(StringUtil.getSplitePrefix(ContextUtil.zkIpPort, Cat.colon))
                .namespace("sgs")
                .sessionTimeoutMs(60 * 1000)
                .retryPolicy(retryPolicy).build();
        return curator;
    }


    public void init(ServerManager serverManager) throws Exception {
        curator = getCurator();
        curator.start();

        //递规创建路径，用在第一次在系统中启动时创建路径
        curator.checkExists().creatingParentContainersIfNeeded().forPath(Constant.ZOOKEEPER_PATH);

        //加入路径监听
        childrenCache = new PathChildrenCache(curator, Constant.ZOOKEEPER_PATH, true);
        try {
            childrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final AtomicBoolean first = new AtomicBoolean(true);
        childrenCache.getListenable().addListener(
                (client, event) -> {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            if (first.get()) {
                                first.set(false);
                                serverManager.decrAsyncCount();
                            }
                            ServerInfoManager.addServer(GameUtil.transToServerInfo(event.getData().getPath()));
                            break;
                        case CHILD_REMOVED:
                            ServerInfoManager.removeServer(GameUtil.transToServerInfo(event.getData().getPath()));
                            break;
                        default:
                            break;
                    }
                }
        );

        ZookeeperUtil.connectZookeeper(serverInfo);

    }


    @Override
    public void onStart() {

    }


    public void onClose() {
        childrenCache.clear();
        try {
            childrenCache.close();
        } catch (IOException e) {
            log.error("", e);
        }
        curator.close();
    }
}