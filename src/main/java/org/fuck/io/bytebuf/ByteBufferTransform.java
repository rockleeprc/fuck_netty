package org.fuck.io.bytebuf;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class ByteBufferTransform {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        String str = "hello";

        buffer.put(str.getBytes());

        // 自动切换到读取模式
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode(str);

        // 自动切换到读取模式
        ByteBuffer buffer2 = ByteBuffer.wrap(str.getBytes());

        // buffer1、buffer2直接就是读模式，
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer1);
        System.out.println(charBuffer.toString());

        //buffer现在是写模式，需要调用flip转换到读模式
        buffer.flip();
        CharBuffer decode = StandardCharsets.UTF_8.decode(buffer);
        System.out.println(decode);
    }
}
