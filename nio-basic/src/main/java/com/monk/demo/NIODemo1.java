package com.monk.demo;

import java.nio.IntBuffer;

public class NIODemo1 {

    public static void main(String[] args) {
        final IntBuffer intBuffer = IntBuffer.allocate(5);

        for (int i = 0; i < 5; i++) {
            intBuffer.put(i * 2);
        }
        // intBuffer.put(3, 20);
        System.out.println(intBuffer.get(0));
        intBuffer.flip();
        System.out.println(intBuffer.get(0));
        while (intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
        System.out.println(intBuffer.get(0));
    }
}
