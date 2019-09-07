package com.rpc.interfaces.gameToBus;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Rpc;
import com.entry.PlayerEntry;

public interface GameToBus
{
	@Suspendable
	@Rpc(needResponse=true)
	public String needResponse(String a);
	
	@Rpc(needResponse=false)
	Object noNeedResponse(String a);
	
	@Rpc(needResponse=false)
	Object noNeedResponse0();
	
	@Suspendable
	@Rpc(needResponse=true)
	PlayerEntry aaa(String a);
	
}
