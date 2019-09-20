package com.rpc.interfaces.gameToBus;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Rpc;
import com.entry.PlayerEntry;
import com.pojo.OnlineContext;
import com.pojo.Tuple;

public interface GameToBus{
	
	@Suspendable
	@Rpc(needResponse = true)
	default Tuple<Boolean,Throwable> createUnion(long playerId,String unionName) {
		return null;
	}
	
	@Suspendable
	@Rpc(needResponse = true)
	default RpcResult<Boolean,Throwable> ccc(long playerId,String unionName) {
		return null;
	}
	
	@Suspendable
	@Rpc(needResponse = true)
    default String needResponse(String a) {
		return a;
	}
	
	
	@Suspendable
	@Rpc(needResponse = false)
	default Object putOnline(OnlineContext onlineContext){
		return null;
	}
	
	@Suspendable
	@Rpc(needResponse = false)
	default Object offline(long uid){
		return null;
	}
	
	@Suspendable
	@Rpc(needResponse = true)
    default Object noNeedResponse(String a) {
        return null;
    }
	
	@Suspendable
	@Rpc(needResponse = false)
    default Object noNeedResponse0() {
		return null;
    }
	
	@Suspendable
	@Rpc(needResponse = true)
    default PlayerEntry aaa(String a) {
        return null;
    }
	
	
	
}
