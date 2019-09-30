package com.controller;


import com.annotation.Controllor;
import com.entry.UserEntry;
import com.exception.StatusException;
import com.net.msg.LOGIN_MSG;
import com.pojo.Tuple;
import com.rpc.interfaces.gameToBus.GameToLogin;
import com.service.LoginService;
import com.template.templates.type.TipType;
import com.util.CountUtil;
import com.util.support.TipStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
public class LoginController extends BaseController implements GameToLogin {
    @Autowired
    private LoginService loginService;

    @Controllor
    public CompletableFuture<LOGIN_MSG.GTC_LOGIN> login(UidContext context, LOGIN_MSG.CTG_LOGIN req) throws StatusException {
        String username = req.getUsername();
        String password = req.getPassword();
        String sessionId = req.getSessionId();

        LOGIN_MSG.GTC_LOGIN.Builder builder = LOGIN_MSG.GTC_LOGIN.newBuilder();
        CompletableFuture<UserEntry> user = loginService.login(username, password);
        
        builder.setSessionId(sessionId);
        return user.thenApply(userEntry->{
            if (!Objects.isNull(userEntry)) {
                builder.setUid(userEntry.getId());
                builder.setResult(TipStatus.suc());
            } else {
                builder.setResult(TipStatus.fail(TipType.AccountError));
                
            }
            return builder.build();
        });
    }


    @Controllor
    public void login(UidContext context, LOGIN_MSG.TEST_TIME req) {
        CountUtil.count();
        String username = req.getMsg();


    }

    @Controllor
    @Override
    public Tuple<String, Throwable> testResponse(Long playerId) {
        return new Tuple<>(playerId + "abc", null);
    }
}
