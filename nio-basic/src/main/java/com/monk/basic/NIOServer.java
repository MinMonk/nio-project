package com.monk.basic;

import com.monk.constant.NIOConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    private static final Logger logger = LoggerFactory.getLogger(NIOServer.class);

    public static void main(String[] args) {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void startServer() throws IOException {

        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        Selector selector = Selector.open();
        serverSocket.bind(new InetSocketAddress(NIOConstants.PORT));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (0 == selector.select(1000)) {
                System.out.println("服务器等待1s,暂无连接");
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    SocketChannel socket = serverSocket.accept();
                    logger.info("新客户端连接" + socket.hashCode());
                    socket.configureBlocking(false);
                    socket.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }

                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    channel.read(byteBuffer);
                    byteBuffer.flip();
                    logger.info("{} - {}", channel.hashCode(), Charset.defaultCharset().decode(byteBuffer));
                    byteBuffer.clear();

                    // SocketChannel socket = (SocketChannel) key.channel();
                    // ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    // socket.read(byteBuffer);
                    // byteBuffer.flip();
                    // logger.info("{}", Charset.defaultCharset().decode(byteBuffer));
                    // socket.register(selector, SelectionKey.OP_READ);
                }

                iterator.remove();
            }

        }


    }

}
