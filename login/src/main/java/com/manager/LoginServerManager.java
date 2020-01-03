package com.manager;

import com.BaseVerticle;
import com.LoginReceiver;
import com.LoginVerticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginServerManager extends ServerManager {

    @Autowired
    private LoginVerticle verticle;

    @Autowired
    private LoginReceiver loginReceiver;

    @Override
    public BaseVerticle getVerticle() {
        return verticle;
    }

    @Override
    public void onServerStart() {
        super.onServerStart();
        loginReceiver.start();
        //启动器计数
//        startWatch.count();
    }

    @Override
    public void asyncStart() {

    }

    @Override
    public void onServerStop() {
        super.onServerStop();
        log.info("停服完成 -------------------------------------------------------");
    }
    
    @Override
    public void startOver(){
        //DisLock ccc=LockUtil.lock("ccc");
        //try
        //{
        //    ccc.lock();
        //    Thread.sleep(10000);
        //}catch(Exception e){
        //
        //}finally
        //{
        //    ccc.unLock();
        //}
    }
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        onServerStop();
    }
}
