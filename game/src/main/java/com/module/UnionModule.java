package com.module;

import com.dao.PlayerUnionRepository;
import com.entry.BaseEntry;
import com.entry.PlayerEntry;
import com.entry.PlayerUnionEntry;
import com.enums.TypeEnum;
import com.exception.StatusException;
import com.rpc.RpcProxy;
import com.rpc.interfaces.gameToBus.GameToBus;
import com.rpc.interfaces.gameToBus.RpcResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Order(1)
/**
 * 工会模块
 */
public class UnionModule extends BaseModule {


    private PlayerEntry playerEntry;

    @Autowired
    private PlayerUnionRepository playerUnionRepository;

    private PlayerUnionEntry playerUnionEntry;
    
    @Autowired
    private RpcProxy rpcProxy;
    
    @Override
    public void onLoad() {
        player.setUnionModule(this);
        playerUnionEntry = playerUnionRepository.findById(player.getPlayerId()).orElse(new PlayerUnionEntry(player.getPlayerId()));
    }

    public boolean hasUnion(){
        return playerUnionEntry.getUnionId() == 0;
    }
    
    public long getUnionId(){
        if(!hasUnion()){
            return 0;
        }
        return playerUnionEntry.getUnionId();
    }
    
    public void createUnion() throws StatusException{
        //有帮派返回
        //条件不足返回
        //player 直接扣钱
        //向bus发rpc
        GameToBus gameToBus=rpcProxy.proxy(GameToBus.class,player.getPlayerId(),TypeEnum.ServerTypeEnum.BUS,player.getUid());
        RpcResult<Boolean,Throwable> ccc=gameToBus.ccc(player.getPlayerId(),"ccc");
        ccc.onSuccess();
    
    
    }
    
    
    @Override
    public BaseEntry getEntry() {
        return playerUnionEntry;
    }
    
    @Override
    public CrudRepository getRepository(){
        return playerUnionRepository;
    }
    
    @Override
    public void onLogin() {

    }
}
