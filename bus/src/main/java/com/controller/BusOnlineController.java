package com.controller;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Controllor;
import com.annotation.Rpc;
import com.net.msg.BUS_MSG;
import com.pojo.OnlineContext;
import com.rpc.interfaces.gameToBus.GameToBus;
import com.service.BusOnlineService;
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
}
