package com.net;

import com.net.coder.MessageDecoder;
import com.net.coder.MessageEncoder;
import com.util.SpringUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class NettyServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private AtomicBoolean started = new AtomicBoolean(false);


    public void init() {
        log.info("启动netty");
        if (started.compareAndSet(false, true)) {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
//                                ch.pipeline().addLast("ping", new IdleStateHandler(120, 0, 0));
                                ch.pipeline().addLast("decode", new MessageDecoder());
                                ch.pipeline().addLast("encode", new MessageEncoder());
                                ch.pipeline().addLast("com.handler", new NettyServerMsgHandler(SpringUtils.getBean(ConnectManager.class)));
                            }
                        }).option(ChannelOption.SO_BACKLOG, 10240)
                        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) // 使用内存池
                        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) // 使用内存池
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.TCP_NODELAY, true);

                ChannelFuture f = b.bind(SpringUtils.getBean(SocketAddress.class)).sync();

                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("", e);
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }

    public void stop() {
        if (started.compareAndSet(true, false)) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


}
