package com.fuck.netty.bytebuf;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 处理粘包、半包
 */
public class ByteBufferReadExample {

    public static void main(String[] args) {
        String str1 = "hello,java\nhello,spark\nhel";
        String str2 = "lo,spring\n";
        ByteBuffer buffer = ByteBuffer.allocate(50);
        buffer.put(str1.getBytes());
        split(buffer);
        buffer.put(str2.getBytes());
        split(buffer);
    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            if (buffer.get(i) == '\n') {
                int length = i + 1 - buffer.position();
                System.out.println("length=" + length);
                ByteBuffer tempBuffer = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    tempBuffer.put(buffer.get());
                }
                printBuffer(tempBuffer);
            }
        }
        buffer.compact();
    }

    public static void printBuffer(ByteBuffer buffer) {
        buffer.flip();
        CharBuffer decode = StandardCharsets.UTF_8.decode(buffer);
        System.out.println(decode);
        buffer.clear();
    }
}
