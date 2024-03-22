package com.monk.basic;

import com.monk.constant.NIOConstants;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {

    public static void main(String[] args) throws Exception {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress(InetAddress.getLoopbackAddress(), NIOConstants.PORT);
        if (!channel.connect(address)) {
            while (!channel.finishConnect()) {
                System.out.println("客户端不会阻塞,可以做其他事情");
            }
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap("hello,和尚".getBytes());
        // byteBuffer.flip();
        channel.write(byteBuffer);
        System.in.read();

    }
}
