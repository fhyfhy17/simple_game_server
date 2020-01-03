package com.manager;

import com.BaseVerticle;
import com.config.ZookeeperConfig;
import com.controller.ControllerFactory;
import com.dao.cache.CacheCenter;
import com.enums.TypeEnum;
import com.node.Node;
import com.service.BaseService;
import com.util.ContextUtil;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

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
    
//    @Getter
//    private ScheduleAble threadSchedule;//专门一个的线程，放schedule的
    
    protected AtomicInteger count = new AtomicInteger(0);
    
//    protected StartWatch startWatch;
    
    //服务器启动
    public void onServerStart() {
        //计数监控启动
//        startWatch = new StartWatch();
//        count=startWatch.init();

        asyncInit();

        //启动node
        getVerticle().init();

        //启动service的start方法
        if (!Objects.isNull(services)) {
            services.forEach(BaseService::onStart);
        }

        //启动消息注册器
        ControllerFactory.init();


    }

    private void asyncInit(){
        //启动连接
        new Thread(() -> {
            try {
                incrAsyncCount();
                zookeeperConfig.init(this);
            } catch (Exception e) {
                log.error("", e);
            }
        }).start();

        asyncStart();
    }

    public abstract void asyncStart();


    public void incrAsyncCount(){
        count.incrementAndGet();
    }

    public void decrAsyncCount(){
        checkStartOver(count.decrementAndGet());
    }

    private void checkStartOver(int count){
        if(count != 0){
            return;
        }
        log.info(ContextUtil.id + "服务器启动成功");
        //这要不要做成个事件
        serverStatus = TypeEnum.ServerStatus.OPEN;
        startOver();
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
//     class StartWatch{
//
//        private AtomicInteger count;
//
//        public AtomicInteger init(){
//            threadSchedule= new DefaultScheduleAble();
//            threadSchedule.schedulerListInit();
//            new Thread(threadSchedule::tickSchedule,"startStopWatch 线程").start();
//            count = new AtomicInteger(0);
//            return count;
//        }
//
//
//        public void count(){
//            threadSchedule.schedulePeriod(new ScheduleTask(){
//                @Override
//                public void execute(){
//                    ScheduleTask scheduleTask=ContextHolder.getScheduleTask();
//                    if(count.get()==0){
//                        log.info("服务器启动成功");
//                        try{
//                            threadSchedule.deleteSchedulerJob(scheduleTask.jobKey);
//                        } catch(SchedulerException e){
//                            log.error("",e);
//                        }
//                        //这要不要做成个事件
//                        serverStatus = TypeEnum.ServerStatus.OPEN;
//                        startOver();
//                    }
//                }
//            },1000,200);
//        }
//
//    }
    //完全启动成功后
    public abstract void startOver();
    
    public TypeEnum.ServerStatus getServerStatus() {
        return serverStatus;
    }
    
    public void setServerStatus(TypeEnum.ServerStatus serverStatus){
        this.serverStatus = serverStatus;
    }
}