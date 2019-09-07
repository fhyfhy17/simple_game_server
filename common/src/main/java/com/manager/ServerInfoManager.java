package com.manager;

import cn.hutool.core.util.RandomUtil;
import com.enums.TypeEnum;
import com.node.RemoteNode;
import com.pojo.Packet;
import com.pojo.ServerInfo;
import com.util.ContextUtil;
import com.util.SerializeUtil;
import com.util.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ServerInfoManager {


    private static ConcurrentHashMap<String, ServerInfo> serverInfos = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, RemoteNode> remotes = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ServerInfo> getAllServerInfos() {
        return serverInfos;
    }

    public static boolean ifCached(String serverId) {
        return serverInfos.values().stream().anyMatch(x -> x.getServerId().equals(serverId));
    }

    public static void addServer(ServerInfo serverInfo) {

        String hostAddress = serverInfo.getIp() + ":" + serverInfo.getPort();
        if (serverInfo.getServerId() != ContextUtil.id) {
            RemoteNode remoteNode = new RemoteNode(hostAddress);
            remotes.put(serverInfo.getServerId(), remoteNode);
            remoteNode.startup();
        }

        serverInfos.put(serverInfo.getServerId(), serverInfo);
        log.info("新服务加入={}  ,所有服务={}", serverInfo.getServerId(), serverInfos);
    }

    public static void removeServer(ServerInfo serverInfo) {
        serverInfos.remove(serverInfo.getServerId());
        log.info("服务退出={}  ,所有服务={}", serverInfo, serverInfos);
        if (serverInfo.getServerId().equals(ContextUtil.id)) {
            ZookeeperUtil.connectZookeeper(serverInfo);
        }

    }

    /**
     * 随机一个服务
     */
    public static ServerInfo randomServerInfo(TypeEnum.ServerTypeEnum serverType) {
        List<ServerInfo> list = getAllServerInfos().values().stream().filter(
                x -> x.getServerType() == serverType
        ).collect(Collectors.toList());
        return list.size() < 1 ? null : RandomUtil.randomEle(list);
    }

    public static List<ServerInfo> getServerInfosByType(TypeEnum.ServerTypeEnum serverType) {
        return getAllServerInfos().values().stream().filter(
                x -> x.getServerType() == serverType
        ).collect(Collectors.toList());
    }

    public static ServerInfo getServerInfo(String serverId) {
        return serverInfos.get(serverId);
    }

    public static String hashChooseServer(Object key, TypeEnum.ServerTypeEnum typeEnum) {
        List<ServerInfo> list = getServerInfosByType(typeEnum);
        if (list.size() < 1) {
            log.error("所有 {} 服务器都挂了", typeEnum);
            return null;
        }
        int index = (key.hashCode() % list.size());
        ServerInfo info = list.get(index);
        return info.getServerId();
    }

    public static void printServerStatus(ServerInfo serverInfo, boolean join) {
        String action = join ? "加入" : "离开";
        log.info(action + " 服务器 serverType= {} , serverId= {}", serverInfo.getServerId(), serverInfo.getServerType());
        log.info("当前所有的服务器={}", getAllServerInfos());
    }

    public static RemoteNode getRemoteNode(String serverId) {
        return remotes.get(serverId);
    }


    public static void sendMessage(String queue, Packet message) {
        RemoteNode remoteNode = ServerInfoManager.getRemoteNode(queue);
        remoteNode.sendReqMsg(SerializeUtil.mts(message));
    }
}