package org.fuck.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();) {
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(8899));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            for (; ; ) {
                if (selector.select(1000) == 0) {
                    System.out.println("等待1s，无连接");
                    continue;
                }

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel channel = serverChannel.accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                        System.out.println("收到客户端连接");
                    }
                    if (selectionKey.isReadable()) {
                        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        int length = channel.read(buffer);
                        System.out.println("服务器端收到消息：" + new String(buffer.array(), 0, length));
                    }
                    iterator.remove();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
