package com.controller;

import com.annotation.Controllor;
import com.entry.UnionEntry;
import com.pojo.Tuple;
import com.rpc.interfaces.gameToBus.GameToBus;
import com.service.UnionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class UnionController extends BaseController implements GameToBus {

    @Autowired
    private UnionService unionService;

    @Controllor
    @Override
    public Tuple<UnionEntry, Throwable> createUnion(long playerId, String unionName) {
        return unionService.createUnion(playerId, unionName);
    }
}
