package com;

import com.net.msg.LOGIN_MSG;
import com.pojo.Packet;
import com.util.ProtoUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Sharable
@Slf4j
public class NettyClientMsgHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        final ChannelHandlerContext fctx = ctx;
        ClientSession.getInstance();
        //启动ClientSession 和 Scanner交互器。
        ClientSession.init(fctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            Packet message = (Packet) msg;
            if (message.getId() == 10009) {


                LOGIN_MSG.GTC_PLAYER_LIST stc_player_list = LOGIN_MSG.GTC_PLAYER_LIST.parseFrom(message.getData());
                LOGIN_MSG.PLAYER_INFO player = stc_player_list.getPlayers(0);
                long playerId = player.getPlayerId();
                LOGIN_MSG.CTG_GAME_LOGIN_PLAYER.Builder loginBuilder = LOGIN_MSG.CTG_GAME_LOGIN_PLAYER.newBuilder();
                loginBuilder.setPlayerId(playerId);

                NettyMessage m = new NettyMessage();
                m.setId(ProtoUtil.protoGetMessageId(loginBuilder));
                m.setData(loginBuilder.build().toByteArray());
                m.setAutoIncrease(ClientSession.getAutoIncrease() + 1);
                ClientSession.setAutoIncrease(ClientSession.getAutoIncrease() + 1);
                m.setCheckCode(ClientSession.buildCheckCode(m));


                ctx.channel().writeAndFlush(m);

            }
            if (message.getId() == 10002) {


                LOGIN_MSG.GTC_LOGIN login = LOGIN_MSG.GTC_LOGIN.parseFrom(message.getData());
                long uid = login.getUid();
                LOGIN_MSG.CTG_PLAYER_LIST.Builder csPl = LOGIN_MSG.CTG_PLAYER_LIST.newBuilder();


                NettyMessage m = new NettyMessage();
                m.setId(ProtoUtil.protoGetMessageId(csPl));
                m.setData(csPl.build().toByteArray());
                m.setAutoIncrease(ClientSession.getAutoIncrease() + 1);
                ClientSession.setAutoIncrease(ClientSession.getAutoIncrease() + 1);
                m.setCheckCode(ClientSession.buildCheckCode(m));


                ctx.channel().writeAndFlush(m);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

}
