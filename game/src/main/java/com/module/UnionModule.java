package com.module;

import com.dao.PlayerUnionRepository;
import com.entry.BaseEntry;
import com.entry.PlayerEntry;
import com.entry.PlayerUnionEntry;
import com.entry.UnionEntry;
import com.enums.TypeEnum;
import com.pojo.Tuple;
import com.rpc.RpcProxy;
import com.rpc.interfaces.player.GameToBus;
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

    public void createUnion(String unionName) throws Throwable {
        //有帮派返回
        //条件不足返回
        //player 直接扣钱
        //向bus发rpc
        GameToBus gameToBus=rpcProxy.proxy(GameToBus.class,player.getPlayerId(),TypeEnum.ServerTypeEnum.BUS,player.getUid());

        //rpc调用要对结果进行异常判断，失败把之前的操作进行补偿
        Tuple<UnionEntry, Throwable> result = gameToBus.createUnion(player.getPlayerId(), unionName);
        if (result.getValue() != null) {
            //player 把钱加回来
            throw result.getValue();
        }

        //继续创建帮派流程
        UnionEntry unionEntry = result.getKey();
        playerUnionEntry.setUnionId(unionEntry.getId());
        playerUnionEntry.setUnionLevel(unionEntry.getLevel());
        playerUnionEntry.setUnionName(unionEntry.getName());

        playerUnionRepository.save(playerUnionEntry);

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
