package com.fuck.netty.bytebuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class ByteBufferReadFile {
    public static void main(String[] args) {
        FileChannel fileChannel = null;
        ByteBuffer buffer = ByteBuffer.allocate(10);
        try {
            fileChannel = FileChannel.open(Paths.get("data.txt"));
            while (true) {
                int len = fileChannel.read(buffer); // 读取数据到buffer，读到eof返回-1
                if (len == -1)
                    break;
                buffer.flip();// 切换读模式
                while (buffer.hasRemaining()) {
                    System.out.println((char) buffer.get());
                }
                // TODO 在循环的开始clear可能更好
                buffer.clear();// 读取完成后切换写模式，为下一读取准备
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
