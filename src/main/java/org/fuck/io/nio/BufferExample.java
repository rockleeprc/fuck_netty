package org.fuck.io.nio;

import java.nio.CharBuffer;
import java.nio.IntBuffer;

public class BufferExample {
    public static void main(String[] args) {


    }

    public static void reverse() {
        char[] chars = "AaBbCcDd".toCharArray();
        CharBuffer buffer = CharBuffer.allocate(chars.length);
        buffer.put(chars);
        System.out.println(new String(buffer.array(), 0, chars.length));
        buffer.flip();// limit = position,positio = 0
        while (buffer.hasRemaining()) {
            buffer.mark();// mark = position
            char c1 = buffer.get();
            char c2 = buffer.get();
            buffer.reset();// position = mark
            buffer.put(c2).put(c1);
        }
        buffer.rewind();// position = 0,mark = -1
        System.out.println(new String(buffer.array(), 0, chars.length));
    }


    public static void m1() {
        IntBuffer buf = IntBuffer.allocate(10);
        for (int i = 0; i < 5; i++) {
            buf.put(i + i);
        }

        buf.flip();// 读写转换

        while (buf.hasRemaining()) {
            System.out.println(buf.get());
        }

        buf.clear();// 清空buffer
    }
}
