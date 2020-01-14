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

    public static final String RPC_REQUEST= "rpcRequest";
    
    public static final String RPC_RESPONSE = "rpcResponse";

    public static final String IO_THREAD_NAME = "ioThreadPool";

    public static final String CONTEXT_SCHEDULE_ABLE = "contextScheduleAble";
    
    public static final String CONTEXT_SCHEDULE_TASK = "contextScheduleTask";
    
    public static final String SCHEDULE_PULSE_PARAM = "schedulePulseParam";

    public static final int ZSET_MAX_LEVEL = 32;
    public static final int ZSET_INIT_CAPACITY = 128;
    public static final float ZSET_SKIPLIST_P = 0.25f;

}
