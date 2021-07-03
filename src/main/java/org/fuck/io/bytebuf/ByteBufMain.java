package org.fuck.io.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class ByteBufMain {
    public static void main(String[] args) {
        ByteBuf buf = Unpooled.copiedBuffer("hello netty", CharsetUtil.UTF_8);
        // 对内存模式
        if (buf.hasArray()) {
            byte[] array = buf.array();
            int offset = buf.arrayOffset() + buf.readerIndex();
            int length = buf.readableBytes();
            System.out.println(new String(array, offset, length));
        }

        System.out.println("capacity:"+buf.capacity());
        System.out.println("readableBytes:"+buf.readableBytes());
        System.out.println("writableBytes:"+buf.writableBytes());
        System.out.println("discardReadBytes:"+buf.discardReadBytes());// 可丢弃的数据，可被回收

        // 随机访问索引数据
        for (int i = 0; i < buf.capacity(); i++) {
            System.out.print((char) buf.getByte(i) + "-");
        }

        System.out.println("\r\n");

        // 读取所有数据
        while(buf.isReadable()){
            System.out.print((char)buf.readByte()+"-");
        }
    }

}
