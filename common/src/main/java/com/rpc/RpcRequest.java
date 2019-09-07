package com.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest
{
	private String id;
	private String className;
	private String methodName;
	private Object[] parameters;
}