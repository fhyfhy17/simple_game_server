package com.disruptor;

import com.pojo.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageEvent extends BaseEvent {
    private Packet message;
}
