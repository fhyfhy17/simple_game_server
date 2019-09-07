package com;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class NettyClient {

    private final static int connectTimeoutMillis = 10 * 1000;
    Bootstrap b;
    EventLoopGroup workerGroup;
    boolean conected;
    Channel channel;

    public NettyClient(String host, int port) {
        workerGroup = new NioEventLoopGroup();

        try {
            b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("decode", new MessageDecoder());
                    ch.pipeline().addLast("encode", new MessageEncoder());
                    ch.pipeline().addLast(new NettyClientMsgHandler());

                }
            });
            b.remoteAddress(host, port);
            ChannelFuture future = b.connect();
            future.awaitUninterruptibly();
            conected = future.isSuccess();
            channel = future.channel();
        } catch (Exception e) {

            workerGroup.shutdownGracefully();
        }

    }
}
