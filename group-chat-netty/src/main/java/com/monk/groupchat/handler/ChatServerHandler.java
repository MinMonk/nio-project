package com.monk.groupchat.handler;

import com.monk.util.SocketUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("{} 上线了", SocketUtils.remoteAddress(ctx.channel()));
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("{} 下线了", SocketUtils.remoteAddress(ctx.channel()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msg = String.format("%s [%s] 加入了聊天室", sf.format(new Date()), SocketUtils.remoteAddress(ctx.channel()));
        this.groupSendMsg(ctx.channel(), msg, true);
        channelGroup.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String msg = String.format("%s [%s] 离开了聊天室", sf.format(new Date()), SocketUtils.remoteAddress(ctx.channel()));
        this.groupSendMsg(ctx.channel(), msg, true);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("产生了异常", cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("收到了消息:{}", (String) msg);
        // 将消息进行转发
        this.groupSendMsg(ctx.channel(), (String) msg, false);
    }

    /**
     * 群发消息
     *
     * @param self        自己
     * @param msg         消息内容
     * @param excludeSelf 是否排除发给自己
     */
    private void groupSendMsg(Channel self, String msg, boolean excludeSelf) {
        channelGroup.stream().forEach(ch -> {
            boolean isSelf = ch == self;
            if (excludeSelf && isSelf) {
                return;
            }
            ch.writeAndFlush(isSelf ? msg.replaceAll("\\[(.*)\\]", "自己") : msg);
        });
    }
}
