package com.controller;


import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Controllor;
import com.annotation.Rpc;
import com.entry.PlayerEntry;
import com.entry.UserEntry;
import com.exception.StatusException;
import com.net.msg.LOGIN_MSG;
import com.rpc.interfaces.gameToBus.GameToBus;
import com.service.LoginService;
import com.template.templates.type.TipType;
import com.util.CountUtil;
import com.util.IdCreator;
import com.util.TipStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
public class LoginController extends BaseController implements GameToBus{
    @Autowired
    private LoginService loginService;

    @Controllor
    @Async
    public CompletableFuture<LOGIN_MSG.GTC_LOGIN> login(UidContext context,LOGIN_MSG.CTG_LOGIN req) throws StatusException {
        String username = req.getUsername();
        String password = req.getPassword();
        String sessionId = req.getSessionId();

        LOGIN_MSG.GTC_LOGIN.Builder builder = LOGIN_MSG.GTC_LOGIN.newBuilder();
        //CompletableFuture<UserEntry> user = loginService.login(username, password);
        UserEntry user = loginService.login(username, password);
        builder.setSessionId(sessionId);
        
    
        if (!Objects.isNull(user)) {
            builder.setResult(TipStatus.suc());
            builder.setUid(user.getId());
        }else {
            builder.setResult(TipStatus.fail(TipType.AccountError));
        }

        //user.
        //
        //        whenCompleteAsync((userEntry, throwable) -> {
        //
        //    if (!Objects.isNull(userEntry)) {
        //        builder.setUid(userEntry.getId());
        //        builder.setResult(TipStatus.suc());
        //    } else {
        //        builder.setResult(TipStatus.fail(TipType.AccountError));
        //    }
        //
        //});


        return CompletableFuture.completedFuture(builder.build());

    }


    @Controllor
    public void login(UidContext context, LOGIN_MSG.TEST_TIME req) {
        CountUtil.count();
        String username = req.getMsg();


    }
    
    @Controllor
    @Rpc(needResponse=true)
    @Suspendable
    @Override
    public String needResponse(String a){
        //log.info("neeResponse : "+ a);
        return a;
    }
    @Controllor
    @Rpc(needResponse=false)
    @Override
    public Object noNeedResponse(String a){
        log.info("异步");
        return null;
    }
    
    @Controllor
    @Rpc(needResponse=false)
    @Override
    public Object noNeedResponse0(){
        log.info("异步0");
        return null;
    }
    
    @Controllor
    @Rpc(needResponse=false)
    @Suspendable
    @Override
    public PlayerEntry aaa(String a){
        System.out.println("有点慢啊");
        return new PlayerEntry(IdCreator.nextId(PlayerEntry.class));
    }
}
