package com.monk.groupchat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 群聊程序客户端
 *
 * @author monk
 */
public class GroupChatClient extends GroupChat {

    private Selector selector;
    private SocketChannel socketChannel;
    private String clientName;

    public GroupChatClient() {
        try {
            this.selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            clientName = super.clientAddress(socketChannel.getLocalAddress());
            System.out.println(clientName + " is ready...");
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
                        if (selectionKey.isReadable()) {
                            // 读事件,读取消息,并将其转发到在线的其他客户端
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            int readLen = channel.read(byteBuffer);
                            if (readLen > 0) {
                                String msg = new String(byteBuffer.array()).trim();
                                System.out.println(msg);
                            }
                        }

                        iterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void sendMsg() {
        Scanner scanner = new Scanner(System.in);
        String msg = "%s 说: %s";
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            try {
                sendMsg(String.format(msg, clientName, input), socketChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        GroupChatClient chatClient = new GroupChatClient();

        // 启动线程监听服务器发送的消息
        new Thread(() -> {
            chatClient.listening();
        }).start();

        // 客户端主线程启动控制台,接受用户的输入来发消息
        chatClient.sendMsg();
    }
}
