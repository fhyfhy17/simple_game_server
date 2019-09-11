package com.controller;

import com.annotation.Controllor;
import com.annotation.Rpc;
import com.net.msg.BUS_MSG;
import com.pojo.OnlineContext;
import com.service.BusOnlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
public class BusOnlineController extends BaseController {

    @Autowired
    private BusOnlineService busOnlineService;

    @Controllor
    @Rpc
    public void putOnline(OnlineContext onlineContext) {
        busOnlineService.putOnlineContext(onlineContext);
    }

    @Controllor
    @Rpc
    public void offline(UidContext uidContext,BUS_MSG.GTB_OFFLINE req) {
        busOnlineService.delOnlineContext(req.getUid());
    }

    @Controllor
    public void onlineHeart(UidContext uidContext,BUS_MSG.GTB_ONLINE_UIDS_HEART req){
        String from = uidContext.getFrom();
        List<Long> uidsList = req.getUidsList();
        busOnlineService.onHeart(from,uidsList);
    }
}
