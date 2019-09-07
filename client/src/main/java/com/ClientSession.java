package com;

import com.net.msg.LOGIN_MSG;
import com.pojo.Packet;
import io.netty.channel.Channel;

import java.util.Scanner;
import java.util.zip.CRC32;

public class ClientSession {
    private static final ClientSession session = new ClientSession();
    private long playerId;
    public static ClientSession getInstance() {
        return session;
    }

    public static void init(final Channel channel) {
        ClientSession.getInstance().setUid("a123");
        ClientSession.getInstance().setChannel(channel);
        System.out.println("客户端打开连接");
        new Thread(new Runnable() {

            @Override
            public void run() {
                @SuppressWarnings("resource")
                Scanner input = new Scanner(System.in);
                System.out.println("输入1登录 输入2登出");
                while (input.hasNext()) {
                    System.out.println("输入1登录 输入2登出");
                    String line = input.next();
//                    if (!("1".equals(line) || "2".equals(line))) {
//                        System.out.println("输入错误，请重新输入1登录 输入2登出   ");
//                    }
                    if ("1".equals(line)) {
                        LOGIN_MSG.CTG_LOGIN.Builder builder = LOGIN_MSG.CTG_LOGIN.newBuilder();
                        builder.setUsername("bbbbb");
                        builder.setPassword("1a");

                        NettyMessage m = new NettyMessage();
                        m.setId(10001);
                        m.setData(builder.build().toByteArray());
                        m.setAutoIncrease(getAutoIncrease() + 1);
                        setAutoIncrease(getAutoIncrease() + 1);
                        m.setCheckCode(buildCheckCode(m));
                        channel.writeAndFlush(m);
                    }

                    if ("2".equals(line)) {
                        for (int i = 0; i < 1; i++) {
                            LOGIN_MSG.CTG_TEST.Builder builder = LOGIN_MSG.CTG_TEST.newBuilder();
                            builder.setWord("啊啊等等");
                            NettyMessage m = new NettyMessage();
                            m.setId(10005);
                            m.setData(builder.build().toByteArray());
                            m.setAutoIncrease(getAutoIncrease() + 1);
                            setAutoIncrease(getAutoIncrease() + 1);
                            m.setCheckCode(buildCheckCode(m));
                            channel.writeAndFlush(m);
                        }
                    }
                    if ("3".equals(line)) {

                        LOGIN_MSG.CTG_PLAYER_LIST.Builder builder = LOGIN_MSG.CTG_PLAYER_LIST.newBuilder();

                        NettyMessage m = new NettyMessage();
                        m.setId(10008);
                        m.setData(builder.build().toByteArray());
                        m.setAutoIncrease(getAutoIncrease() + 1);
                        setAutoIncrease(getAutoIncrease() + 1);
                        m.setCheckCode(buildCheckCode(m));
                        channel.writeAndFlush(m);

                    }
                    if ("4".equals(line)) {

                        LOGIN_MSG.CTG_PlayerInfo.Builder builder = LOGIN_MSG.CTG_PlayerInfo.newBuilder();

                        NettyMessage m = new NettyMessage();
                        m.setId(10012);
                        m.setData(builder.build().toByteArray());
                        m.setAutoIncrease(getAutoIncrease() + 1);
                        setAutoIncrease(getAutoIncrease() + 1);
                        m.setCheckCode(buildCheckCode(m));
                        channel.writeAndFlush(m);

                    }
                }
            }
        }).start();

    }

    private String uid;
    private Channel channel;
    private static int autoIncrease;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public static int getAutoIncrease() {
        return autoIncrease;
    }

    public static void setAutoIncrease(int autoIncrease) {
        ClientSession.autoIncrease = autoIncrease;
    }

    public static long buildCheckCode(Packet message) {
        CRC32 crc32 = new CRC32();
        crc32.update(message.getData());
        return crc32.getValue();
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
