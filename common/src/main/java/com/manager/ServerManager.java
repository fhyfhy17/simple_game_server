package com.manager;

import com.BaseVerticle;
import com.config.ZookeeperConfig;
import com.controller.ControllerFactory;
import com.dao.cache.CacheCenter;
import com.enums.TypeEnum;
import com.node.Node;
import com.service.BaseService;
import com.thread.schedule.DefaultScheduleAble;
import com.thread.schedule.ScheduleAble;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class ServerManager {

    @Autowired
    private Node node;
    @Autowired
    private CacheCenter cacheCenter;
    
    private TypeEnum.ServerStatus serverStatus = TypeEnum.ServerStatus.STARTING;

    @Autowired(required = false)
    private List<BaseService> services;

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    public abstract BaseVerticle getVerticle();
    
    @Getter
    private ScheduleAble startStopScheduleAble;
 
    
    //服务器启动
    public void onServerStart() {
        //启动监控启动成功
        startStopScheduleAble = new DefaultScheduleAble();
        startStopScheduleAble.schedulerListInit();
        new Thread(startStopScheduleAble::pulseSchedule,"startStopWatch 线程").start();
        
        //启动node
        getVerticle().init();

        //启动service的start方法
        if (!Objects.isNull(services)) {
            services.forEach(BaseService::onStart);
        }
        //启动连接
        new Thread(() -> {
            try {
                zookeeperConfig.init();
            } catch (Exception e) {
                log.error("", e);
            }
        }).start();
        
        //启动消息注册器
        ControllerFactory.init();
    
    }
    
    
    //服务器关闭
    public void onServerStop() {
        log.info("停服中 --------------------------------");
        //关闭service的stop方法
        if(!Objects.isNull(services)){
            services.forEach(BaseService::onClose);
        }
    
        try{
            node.stop();
        }
        catch(Exception e){
            log.error("",e);
        }
    
    
        log.info("开始强制入库");
        //强制入库
        cacheCenter.batchSave();
        
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }

    public TypeEnum.ServerStatus getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(TypeEnum.ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }
}