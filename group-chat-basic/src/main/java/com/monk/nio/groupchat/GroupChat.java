package com.monk.nio.groupchat;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 群聊程序父类,封装了些公共方法
 *
 * @author monk
 */
public class GroupChat {

    protected static final Integer PORT = 9091;

    protected String clientAddress(SocketAddress remoteAddress) {
        return remoteAddress.toString().substring(1);
    }

    protected static void sendMsg(String msg, SocketChannel channel) throws IOException {
        channel.write(ByteBuffer.wrap(msg.getBytes()));
    }
}
