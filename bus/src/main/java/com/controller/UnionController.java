package com.controller;

import com.annotation.Controllor;
import com.entry.UnionEntry;
import com.rpc.interfaces.player.GameToBus;
import com.service.UnionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
public class UnionController extends BaseController implements GameToBus {

    @Autowired
    private UnionService unionService;

    @Controllor
    @Override
    public CompletableFuture<UnionEntry> createUnion(Long playerId,String unionName) {
        return unionService.createUnion(playerId, unionName);
    }
}
