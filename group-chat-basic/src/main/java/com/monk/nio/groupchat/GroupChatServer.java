package com.monk.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 群聊程序服务端
 *
 * @author monk
 */
public class GroupChatServer extends GroupChat {

    private Selector selector;
    private ServerSocketChannel serverChannel;

    public GroupChatServer() {
        try {
            this.selector = Selector.open();
            this.serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(PORT));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器启动成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listening() {
        while (true) {
            try {
                int eventNum = selector.select();
                if (eventNum > 0) {
                    // 当时间数量大于0时,说明有事件触发
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isAcceptable()) {
                            SocketChannel acceptChannel = serverChannel.accept();
                            acceptChannel.configureBlocking(false);
                            acceptChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println(super.clientAddress(acceptChannel.getRemoteAddress()) + " 上线了");
                        }

                        if (selectionKey.isReadable()) {
                            // 读事件,读取消息,并将其转发到在线的其他客户端
                            this.readMsg(selectionKey);
                        }

                        iterator.remove();
                    }


                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMsg(SelectionKey selectionKey) {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            int readLen = channel.read(byteBuffer);
            if (readLen > 0) {
                // 转发到其他客户端
                this.redirectToOtherClient(new String(byteBuffer.array()).trim(), channel);
            }
        } catch (IOException e) {
            try {
                System.out.println(super.clientAddress(channel.getRemoteAddress()) + " 离线了");
                selectionKey.cancel();
                channel.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 转发消息(排除服务端及当前客户端)
     *
     * @param msg  消息
     * @param self 当前客户端
     */
    private void redirectToOtherClient(String msg, Channel self) throws IOException {
        Iterator<SelectionKey> iterator = selector.keys().iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next().channel();
            if (channel == self || channel instanceof ServerSocketChannel) {
                continue;
            }

            sendMsg(msg, (SocketChannel) channel);
        }
    }

    public static void main(String[] args) {
        new GroupChatServer().listening();
    }
}
