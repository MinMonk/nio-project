package com.monk.groupchat;

import com.monk.constant.NIOConstants;
import com.monk.groupchat.handler.ChatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {

    private ServerBootstrap serverBootstrap;
    private EventLoopGroup boosGroup;
    private EventLoopGroup workGroup;

    public ChatServer() {
        boosGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
    }

    public void start() {
        try {
            serverBootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new ChatServerHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(NIOConstants.NETTY_CHAT_PORT);
            log.info("服务端[{}]准备就绪...", NIOConstants.NETTY_CHAT_PORT);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("", e);
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        new ChatServer().start();
    }
}
