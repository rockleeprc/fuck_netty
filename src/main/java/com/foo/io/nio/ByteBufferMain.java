package com.foo.io.nio;

import java.nio.IntBuffer;

public class ByteBufferMain {
    public static void main(String[] args) {
        IntBuffer buf = IntBuffer.allocate(10);
        for (int i = 0; i < 5; i++) {
            buf.put(i + i);
        }

        buf.flip();

        while (buf.hasRemaining()) {
            System.out.println(buf.get());
        }

        buf.clear();
    }
}
