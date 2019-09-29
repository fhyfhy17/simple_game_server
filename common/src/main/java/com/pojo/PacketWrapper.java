package com.pojo;

import com.controller.ControllerHandler;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PacketWrapper{
	private Packet packet;
	private ControllerHandler handler;
	private Object[] m;
}
