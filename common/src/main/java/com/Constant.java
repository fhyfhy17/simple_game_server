package com;

import com.google.protobuf.Message;
import com.net.msg.LOGIN_MSG;

public class Constant {
    public static final int GAME_PROTO_BEGIN = 10000;
    public static final String SERVER_MAP = "server_map";
    public static final String CONNECT_USER_MAP = "connect_user_map";

    public static final String SERVER_INFO = "serverInfo";
    public static final String INFINISPAN_CLUSTER_CACHE_NAME = "__vertx.distributed.cache";

    public static final long ID_BEGIN_INDEX = 100;

    public static final int MESSAGE_RECEIVE_DEPLOY_NUM = 5;

    public static final Message DEFAULT_ERROR_REPLY = LOGIN_MSG.GTC_UNIFIED_EXCEPTION.newBuilder().build();

    public static final String ZOOKEEPER_PATH = "/cluster/nodes";

    public static final int RPC_REQUEST_ID= -1;
    
    public static final int RPC_RESPONSE_ID= -2;
}
