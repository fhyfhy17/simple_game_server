package com.rpc;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RpcResponse
{
	private String requestId;
	private Object data;
}