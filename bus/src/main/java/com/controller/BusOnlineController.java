package com.controller;

import com.annotation.Controllor;
import com.net.msg.BUS_MSG;
import com.pojo.OnlineContext;
import com.rpc.interfaces.player.GameToBus;
import com.service.BusOnlineService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class BusOnlineController extends BaseController implements GameToBus {

    @Autowired
    private BusOnlineService busOnlineService;

    @Controllor
    @Override
    public Object putOnline(OnlineContext onlineContext) {
        busOnlineService.putOnlineContext(onlineContext);
        return null;
    }

    @Controllor
    @Override
    public Object offline(Long uid) {
        busOnlineService.delOnlineContext(uid);
        return null;
    }

    @Controllor
    public void onlineHeart(UidContext uidContext, BUS_MSG.GTB_ONLINE_UIDS_HEART req) {
        String from = uidContext.getFrom();
        List<Long> uidsList = req.getUidsList();
        busOnlineService.onHeart(from, uidsList);
    }

    @Controllor
    @Override
    public CompletableFuture<String> needResponse(Long uid) {
        CompletableFuture<String> c=new CompletableFuture<>();
        c.complete("abcde12345");
        return c;
    }
}
