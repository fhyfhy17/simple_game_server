package com.rpc.interfaces.gameToBus;

public abstract class RpcResult<A,B extends Throwable>{
	
	protected A a;
	
	protected B b;
	
	public RpcResult(A a,B b){
		this.a = a;
		this.b = b;
	}
	
	public abstract void onSuccess();
	
	public abstract void onError();
	
	public A getA(){
		return a;
	}
	public B getB(){
		return b;
	}
}
