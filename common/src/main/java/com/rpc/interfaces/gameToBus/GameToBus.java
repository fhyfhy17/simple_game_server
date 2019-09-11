package com.rpc.interfaces.gameToBus;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Rpc;
import com.entry.PlayerEntry;
import com.pojo.OnlineContext;

public interface GameToBus
{
	@Suspendable
	@Rpc(needResponse=true)
    default String needResponse(String a) {
        return a;
    }


    @Suspendable
    @Rpc(needResponse = false)
    default void putOnline(OnlineContext onlineContext) {

    }


    @Suspendable
	@Rpc(needResponse=false)
    default void deleteOnline(long playerId) {

    }

	@Rpc(needResponse=false)
    default Object noNeedResponse(String a) {
        return null;
    }

    @Rpc(needResponse = false)
    default Object noNeedResponse0() {
        return null;
    }
	
	@Suspendable
	@Rpc(needResponse=true)
    default PlayerEntry aaa(String a) {
        return null;
    }
	
}
