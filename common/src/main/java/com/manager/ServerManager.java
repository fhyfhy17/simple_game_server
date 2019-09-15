package com.manager;

import com.BaseVerticle;
import com.config.ZookeeperConfig;
import com.controller.ControllerFactory;
import com.enums.TypeEnum;
import com.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public abstract class ServerManager {

    private TypeEnum.ServerStatus serverStatus = TypeEnum.ServerStatus.STARTING;

    @Autowired
    private List<BaseService> services;

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    public abstract BaseVerticle getVerticle();

    //服务器启动
    public void onServerStart() {
        //启动node
        getVerticle().init();

        //启动service的start方法
        services.forEach(BaseService::onStart);
        //启动连接
        new Thread(() -> {
            try {
                zookeeperConfig.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        //启动消息注册器
        ControllerFactory.init();

    }

    //服务器关闭
    public void onServerStop() {

    }

    public TypeEnum.ServerStatus getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(TypeEnum.ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }
}