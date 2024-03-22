package com.monk.groupchat.handler;

import com.monk.util.SocketUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class ChatClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("===============" + SocketUtils.remoteAddress(ctx.channel().localAddress()) + "===============");
        new Thread(() -> {
            System.out.println("请输入发送的消息文本[exit退出聊天]:");
            final Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                final String command = scanner.nextLine();
                if ("exit".equals(command)) {
                    break;
                }

                String msg = String.format("[%s] 说: %s", SocketUtils.remoteAddress(ctx.channel().localAddress()), command);
                ctx.channel().writeAndFlush(msg);
            }
        }).start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println((String) msg);
    }
}
