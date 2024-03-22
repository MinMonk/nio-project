package com.monk.demo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIODemo2 {

    public static void main(String[] args) throws Exception{
        String srcFile = "/Users/monk/temp/hello.txt";
        String descFile = "/Users/monk/temp/hello_dest.txt";
        // copyFileByChannel(srcFile, descFile);

        copyFileByChannelTransferFrom(srcFile, descFile);
    }

    private static void copyFileByChannelTransferFrom(String srcFile, String descFile) throws IOException {
        final FileChannel srcChannel = new FileInputStream(srcFile).getChannel();
        final FileChannel descChannel = new FileOutputStream(descFile).getChannel();
        descChannel.transferFrom(srcChannel,0, srcChannel.size());
        descChannel.close();
        srcChannel.close();
    }

    private static void copyFileByChannel(String srcFile, String descFile) throws IOException {
        final FileChannel readChannel = new FileInputStream(srcFile).getChannel();
        final FileChannel writeChannel = new FileOutputStream(descFile).getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
        while (true){
            byteBuffer.clear();
            final int read = readChannel.read(byteBuffer);
            if(read == -1){
                break;
            }
            byteBuffer.flip();
            writeChannel.write(byteBuffer);
        }
        writeChannel.close();
        readChannel.close();
    }
}
