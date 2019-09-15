package com.controller;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Controllor;
import com.entry.PlayerEntry;
import com.net.msg.BUS_MSG;
import com.pojo.OnlineContext;
import com.rpc.interfaces.gameToBus.GameToBus;
import com.service.BusOnlineService;
import com.util.IdCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
public class BusOnlineController extends BaseController implements GameToBus{

    @Autowired
    private BusOnlineService busOnlineService;

    @Controllor
    @Suspendable
    @Override
    public Object putOnline(OnlineContext onlineContext) {
        busOnlineService.putOnlineContext(onlineContext);
        return null;
    }

    @Controllor
    @Suspendable
    @Override
    public Object offline(long uid) {
        busOnlineService.delOnlineContext(uid);
        return null;
    }

    @Controllor
    public void onlineHeart(UidContext uidContext,BUS_MSG.GTB_ONLINE_UIDS_HEART req){
        String from = uidContext.getFrom();
        List<Long> uidsList = req.getUidsList();
        busOnlineService.onHeart(from,uidsList);
    }

    @Controllor
    @Override
    public String needResponse(String a) {
        //log.info("neeResponse : "+ a);
        return a;
    }

    @Controllor
    @Override
    public Object noNeedResponse(String a) {
        log.info("异步");
        return null;
    }

    @Controllor
    @Override
    public Object noNeedResponse0() {
        log.info("异步0");
        return null;
    }

    @Controllor
    @Override
    public PlayerEntry aaa(String a) {
        System.out.println("有点慢啊");
        return new PlayerEntry(IdCreator.nextId(PlayerEntry.class));
    }
}
