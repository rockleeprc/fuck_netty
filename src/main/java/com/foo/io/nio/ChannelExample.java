package com.foo.io.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelExample {
    public static void main(String[] args) {

    }

    /**
     * ByteBuffer中的put()数据是强类型的
     */
    public static void BufferOverflowException() {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.putLong(Long.MAX_VALUE);

        buffer.flip();

        buffer.getShort();
    }


    /**
     * 文件copy
     */
    public static void transferFrom() {
        try (
                FileInputStream fis = new FileInputStream("README.md");
                FileOutputStream fos = new FileOutputStream("readme.mm")
        ) {
            FileChannel fisChannel = fis.getChannel();
            FileChannel fosChannel = fos.getChannel();
            // 封装了ByteBuffer操作
            fosChannel.transferFrom(fisChannel, 0, fisChannel.size());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
