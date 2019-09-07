package com.net;


import com.pojo.NettyMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Sharable
@Slf4j
public class NettyServerMsgHandler extends ChannelInboundHandlerAdapter {
    private ConnectManager connectManager;

    public NettyServerMsgHandler(ConnectManager connectManager) {
        super();
        this.connectManager = connectManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("打开连接");
        connectManager.initConnect(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接关闭");
        Channel channel = ctx.channel();
        connectManager.removeConnect(channel);
        try {
            ctx.close();
        } catch (Exception e) {
            log.info("连接关闭", e);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {

            Channel channel = ctx.channel();

            NettyMessage message = (NettyMessage) msg;
            Session session = channel.attr(ConnectManager.USER_ID_KEY).get();
            if (session == null) {
                return;
            }
            connectManager.dealMessage(session, message);

        } catch (Exception ex) {
            log.error("将关闭客户端连接...", ex);
            ctx.close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生 exceptionCaught...", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 心跳处理
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.error("心跳关闭连接...");
                ctx.close();
            }
        }
    }
}
