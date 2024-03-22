package com.monk.groupchat;

import com.monk.constant.NIOConstants;
import com.monk.groupchat.handler.ChatClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatClient {

    private Bootstrap clientBootStrap;
    private EventLoopGroup workGroup;
    private ChannelFuture channelFuture;


    public ChatClient() {
        workGroup = new NioEventLoopGroup();
    }

    public void start() {
        try {
            clientBootStrap = new Bootstrap()
                    .group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new ChatClientHandler());
                        }
                    });

            channelFuture = clientBootStrap.connect("127.0.0.1", NIOConstants.NETTY_CHAT_PORT);
            channelFuture.channel().closeFuture().sync();
            log.info("客户端准备就绪....");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        final ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
