package com.fuck.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 9999));
        sc.write(StandardCharsets.UTF_8.encode("中国人"));
        System.out.println("waiting...");
        System.in.read();
    }
}
