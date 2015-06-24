package com.inubot.net;

import com.inubot.net.handlers.RequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import com.inubot.net.handlers.LoginHandler;

/**
 * @author Septron
 * @since June 21, 2015
 */
public class ScriptDeliveryServer implements Runnable {

    private final EventLoopGroup worker = new NioEventLoopGroup();
    private final EventLoopGroup boss = new NioEventLoopGroup(1);

    private final String address;
    private final int port;

    public ScriptDeliveryServer(String address, int port) {
        this.address = address;
        this.port = port;
    }

    private ServerBootstrap bootstrap() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new RequestHandler());
                        p.addLast(new LoginHandler());
                    }
                });
        return bootstrap;
    }

    public void run() {
        try {
            ChannelFuture future = bootstrap().bind(address, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
