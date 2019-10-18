package com.module;

import com.dao.PlayerUnionRepository;
import com.entry.BaseEntry;
import com.entry.PlayerEntry;
import com.entry.PlayerUnionEntry;
import com.entry.UnionEntry;
import com.enums.TypeEnum;
import com.exception.WrapException;
import com.rpc.RpcProxy;
import com.rpc.interfaces.player.GameToBus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Getter
@Setter
@Order(1)
/**
 * 工会模块
 */
public class UnionModule extends GameModule {


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
    // 异步RPC的代价还是挺大的，写起来复杂，如果调用都是有返回值的，那这调用一串全得改成CompletableFuture，直到不需要返回值的为止。
    // 唯一的好处就是。。。  是无痛的，不阻塞。。。
    public CompletableFuture<UnionEntry> createUnion(String unionName) {
        //有帮派返回
        //条件不足返回
        //player 直接扣钱
        //向bus发rpc
        GameToBus gameToBus=rpcProxy.proxy(GameToBus.class,player.getPlayerId(),TypeEnum.ServerTypeEnum.BUS,player.getUid());

        //rpc调用要对结果进行异常判断，失败把之前的操作进行补偿
        CompletableFuture<UnionEntry> result = gameToBus.createUnion(player.getPlayerId(), unionName);
       return result.whenComplete((unionEntry,throwable)->callBack(()->{
            if(throwable!=null){
                //player 把钱加回来
                throw new WrapException(throwable.getMessage(),throwable);
            }else {
                //继续创建帮派流程
                playerUnionEntry.setUnionId(unionEntry.getId());
                playerUnionEntry.setUnionLevel(unionEntry.getLevel());
                playerUnionEntry.setUnionName(unionEntry.getName());

                playerUnionRepository.save(playerUnionEntry);
            }
        }));
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
