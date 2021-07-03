package org.fuck.io.bytebuf;

import java.nio.ByteBuffer;

public class ByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd', 'e'});
        print(buffer);

        // get(index)不会改变position位置，get()会改变position位置
        System.out.println(buffer.get(2));

        // limit = position; position = 0; 用于读取buffer内容
        buffer.flip();
        byte[] buf = new byte[2];
        buffer.get(buf);
        print(buffer);
        System.out.println(new String(buf, 0, buf.length));

        // position = 0; 重置position，用于重复读取buffer内容
        buffer.rewind();
        buf = new byte[2];
        buffer.get(buf);
        print(buffer);
        System.out.println(new String(buf, 0, buf.length));

        // mark = position，使用mark记录position的位置
        buffer.mark();
        // position = mark，将position指定到之前记录的mark位置，用于从特定位置重复读取
        buffer.reset();
    }

    public static void print(ByteBuffer buffer) {
        System.out.println("position=" + buffer.position() + ",limit=" + buffer.limit() + ",capacity=" + buffer.capacity());
    }
}
