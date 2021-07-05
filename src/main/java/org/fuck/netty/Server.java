package org.fuck.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Server {
    public static void main(String[] args) {
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;
        try {
            // 创建server端进程，设置非阻塞，设置端口号
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(9999));

            // 创建selector，管理多个channel
            selector = Selector.open();
            // 将ServerSocketChannel注册到selector上
            // SelectionKey可以获取哪个channel的哪个事件，ops=0 不关注任何事件
            SelectionKey serverSocketChannelKey = serverSocketChannel.register(selector, 0, null);
            // ServerSocketChannel此时只关注accept事件
            serverSocketChannelKey.interestOps(SelectionKey.OP_ACCEPT);
            System.out.println(serverSocketChannelKey);
            while (true) {
                // 没有任何事件时阻塞；发生事件未处理不会阻塞
                selector.select();
                // 有新事件发生
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // selectedKeys中的事件处理完后不会删除，需要手动删除事件
                    iterator.remove();
//                    System.out.println("key=" + key);
                    // 针对不同事件做不同处理
                    if (key.isAcceptable()) {
                        System.out.println("isAcceptable");
                        acceptable(selector, key);
                    }
                    if (key.isReadable()) {
                        System.out.println("isReadable");
                        try {
                            readable(selector, key);
                        } catch (IOException e) {
                            e.printStackTrace();
                            // client异常断开连接，channel抛异常要cancel，不是正常close
                            key.cancel();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocketChannel != null) {
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void acceptable(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        SelectionKey serverSocketChannel = socketChannel.register(selector, 0, null);
        serverSocketChannel.interestOps(SelectionKey.OP_READ);
    }

    public static void readable(Selector selector, SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(4);
        int length = channel.read(buffer);
        if (length == -1) { // client正常close，读取数据返回-1，关闭channel
            key.cancel();
            System.out.println("client close");
            return;
        }
        buffer.flip();
        CharBuffer decode = StandardCharsets.UTF_8.decode(buffer);
        System.out.println(decode);
    }
}
