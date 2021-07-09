package org.fuck.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteBufExample {
    public static void main(String[] args) {
    }

    public static void t2() {
        // 使用堆内存
        ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer(10);
        log.debug("{}", buffer.getClass());
        log.debug("{}", buffer.readerIndex());
        log.debug("{}", buffer.writerIndex());
        log.debug("{}", buffer.capacity());
    }

    public static void t1() {
        // PooledUnsafeDirectByteBuf，池化基于直接内存
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        // 等同DEFAULT.buffer
//        ByteBuf buffer = ByteBufAllocator.DEFAULT.directBuffer(10);
        log.debug("{}", buffer.getClass());
        log.debug("{}", buffer.readerIndex());
        log.debug("{}", buffer.writerIndex());
        log.debug("{}", buffer.capacity());
    }
}
