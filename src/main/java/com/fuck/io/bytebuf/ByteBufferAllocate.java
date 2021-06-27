package com.fuck.io.bytebuf;

import java.nio.ByteBuffer;

public class ByteBufferAllocate {
    public static void main(String[] args) {
        ByteBuffer buffer1 = ByteBuffer.allocate(10); // HeadByteBuffer，受jvm gc影响（移动数据）
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(10); // DirectByteBuffer，分配效率低
        System.out.println(buffer1.getClass());
        System.out.println(buffer2.getClass());
    }
}
