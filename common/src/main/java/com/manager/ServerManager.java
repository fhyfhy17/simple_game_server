package com.manager;

import com.BaseVerticle;
import com.config.ZookeeperConfig;
import com.controller.ControllerFactory;
import com.dao.cache.CacheCenter;
import com.enums.TypeEnum;
import com.handler.ContextHolder;
import com.node.Node;
import com.service.BaseService;
import com.thread.schedule.DefaultScheduleAble;
import com.thread.schedule.ScheduleAble;
import com.thread.schedule.ScheduleTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class ServerManager implements ApplicationListener<ContextClosedEvent>{

    @Autowired
    private Node node;
    @Autowired
    private CacheCenter cacheCenter;
    
    private volatile TypeEnum.ServerStatus serverStatus = TypeEnum.ServerStatus.STARTING;

    @Autowired(required = false)
    private List<BaseService> services;

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    public abstract BaseVerticle getVerticle();
    
    @Getter
    private ScheduleAble threadSchedule;//专门一个的线程，放schedule的
    
    protected AtomicInteger count;
    //服务器启动
    public void onServerStart() {
        //计数监控启动
        StartWatch startWatch = new StartWatch();
        count=startWatch.init();
    
        //启动node
        getVerticle().init();

        //启动service的start方法
        if (!Objects.isNull(services)) {
            services.forEach(BaseService::onStart);
        }
        //启动连接
        new Thread(() -> {
            try {
                zookeeperConfig.init(count);
            } catch (Exception e) {
                log.error("", e);
            }
        }).start();
        
        //启动消息注册器
        ControllerFactory.init();
        
        //启动器计数
        startWatch.count();
    
    }
    
    
    //服务器关闭
    public void onServerStop() {

        try {
            node.stop();
        } catch (Exception e) {
            log.error("", e);
        }

        //停两秒等残留的所有消息都执行完
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("", e);
        }

        log.info("停服中 --------------------------------");
        //关闭service的stop方法
        if(!Objects.isNull(services)){
            services.forEach(BaseService::onClose);
        }
    
        log.info("开始强制入库");
        //强制入库
        cacheCenter.batchSave();
        
        try {
            Thread.sleep( 1000);
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }
    
    /**
     * 启动监控器
     */
    private class StartWatch{
        
        private AtomicInteger count;
        
        public AtomicInteger init(){
            threadSchedule= new DefaultScheduleAble();
            threadSchedule.schedulerListInit();
            new Thread(threadSchedule::pulseSchedule,"startStopWatch 线程").start();
            count = new AtomicInteger(0);
            return count;
        }
        
        
        public void count(){
            threadSchedule.schedulePeriod(new ScheduleTask(){
                @Override
                public void execute(){
                    ScheduleTask scheduleTask=ContextHolder.getScheduleTask();
                    if(count.get()==0){
                        log.info("服务器启动成功");
                        try{
                            threadSchedule.deleteSchedulerJob(scheduleTask.jobKey);
                        } catch(SchedulerException e){
                            log.error("",e);
                        }
                        //这要不要做成个事件
                        serverStatus = TypeEnum.ServerStatus.OPEN;
                    }
                }
            },2000,200);//由于计数器不是最后启动的，子类还有启动方法，所以可能count的数目不对，所以这里延迟两秒，后续看这个能放到子类不
        }
        
    }
    
    public TypeEnum.ServerStatus getServerStatus() {
        return serverStatus;
    }
    
    public void setServerStatus(TypeEnum.ServerStatus serverStatus){
        this.serverStatus = serverStatus;
    }
}