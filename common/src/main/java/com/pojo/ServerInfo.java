package com.pojo;

import com.enums.TypeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class ServerInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -533104123L;
    private TypeEnum.ServerTypeEnum serverType;
    private String serverId;
    private int type;
    private String ip;
    private int port;
}
