package com.net;

import com.enums.TypeEnum;
import com.google.protobuf.InvalidProtocolBufferException;
import com.handler.MessageGroup;
import com.handler.MessageThreadHandler;
import com.manager.ServerInfoManager;
import com.net.handler.GateMessageHandler;
import com.net.msg.LOGIN_MSG;
import com.net.msg.Options;
import com.pojo.Packet;
import com.pojo.NettyMessage;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ConnectManager {

    public static AttributeKey<Session> USER_ID_KEY = AttributeKey.valueOf("userId");

    private final ConcurrentHashMap<String, Session> idToSessionMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Long, Session> userIdToConnectMap = new ConcurrentHashMap<>();

    private MessageGroup m;
    @Autowired
    private NettyMessageFilter nettyMessageFilter;

    @Value("${netty.needCheck}")
    private boolean needCheck;

    @PostConstruct
    public void startup() {
        m = new MessageGroup(TypeEnum.GroupEnum.GATE_GROUP.name()) {
            @Override
            public MessageThreadHandler getMessageThreadHandler() {
                return new GateMessageHandler();
            }
        };
        m.startup();
    }

    public Session initConnect(Channel channel) {
        //连接注册，绑定session到channel上，可以通过链路 channel取得对应的session信息
        String sessionId = UUID.randomUUID().toString();

        Session session = new Session();
        session.setId(sessionId);
        session.setChannel(channel);

        channel.attr(USER_ID_KEY).set(session);

        idToSessionMap.put(sessionId, session);

        return session;
    }


    //返回登录成功了再注册,多点登录放在login服吧
    public Session register(String sessionId, long uid) {
        //登录注册，保存uid和session到userIdToConnectMap
        Session session = idToSessionMap.get(sessionId);
        session.setUid(uid);
        //绑定一个game服务器
        //保存gameId信息
        String gameId = ServerInfoManager.hashChooseServer(uid, TypeEnum.ServerTypeEnum.GAME);
        session.setGameId(gameId);
        this.userIdToConnectMap.put(uid, session);
        return session;
    }


    public void removeConnect(Channel channel) {
        if (channel == null) {
            return;
        }
        Session session = channel.attr(USER_ID_KEY).get();
        if (session == null) {
            return;
        }
        this.idToSessionMap.remove(session.getId());
        if (session.getUid() == 0) {
            return;
        }
        Session sessionNow = this.userIdToConnectMap.get(session.getUid());
        if (sessionNow.getId().equals(session.getId())) {
            this.userIdToConnectMap.remove(session.getUid());
        }

    }

    public void writeToClient(long uid, Packet message) {
        Session session = userIdToConnectMap.get(uid);
        if (session != null) {
            session.writeMsg(message);
        }
    }


    public void dealUid(Session session, NettyMessage message) throws InvalidProtocolBufferException {
        // 登录流程
        if (message.getId() == LOGIN_MSG.CTG_LOGIN.getDescriptor().getOptions().getExtension(Options.messageId)) {
            //没有uid的时候，先用session做 区分，hash 分发到login
            LOGIN_MSG.CTG_LOGIN.Builder cts_login = LOGIN_MSG.CTG_LOGIN.parseFrom(message.getData()).toBuilder();
            cts_login.setSessionId(session.getId());
            message.setData(cts_login.build().toByteArray());
        } else {
            if (session.getUid() == 0) {
                //TODO 返回消息，请登录
                return;
            }
            message.setUid(session.getUid());
        }
    }

    /**
     * 包检测
     */
    public boolean checkMessage(Session session, NettyMessage message) {

        if (needCheck) {
            // 重复包检测
            if (!nettyMessageFilter.checkAutoIncrease(session, message)) {
                return false;
            }

            // 篡改包检测
            if (!nettyMessageFilter.checkCode(session, message)) {
                return false;
            }
        }
        try {
            //处理Uid
            dealUid(session, message);
        } catch (InvalidProtocolBufferException e) {
            log.error("", e);
            return false;
        }

        return true;

        // 解密  //TODO 加解密甚是爽朗
//        if (session.getPacketEncrypt().isEncrypt()) {
//            session.getPacketEncrypt().decode(packet.getByteArray(), packet.getIncode());
//        }


    }

    public void dealMessage(Session session, NettyMessage message) {
        if (checkMessage(session, message)) {
            m.messageReceived(message);
        }
    }

    /**
     * 多点登录检测
     *
     * @param uid
     * @return
     */
    //TODO 待写
    private boolean loginMultipleCheck(String uid, String sessionId) {
        if (userIdToConnectMap.containsKey(uid)) {
            Session session = userIdToConnectMap.get(uid);
            if (session != null) {
                removeConnect(session.getChannel());
                session.getChannel().close();
                return true;
            }
        }
        return false;
    }

    public ConcurrentHashMap<String, Session> getIdToSessionMap() {
        return idToSessionMap;
    }

    public ConcurrentHashMap<Long, Session> getUserIdToConnectMap() {
        return userIdToConnectMap;
    }
}
