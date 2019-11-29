package com.foo.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 同时只能接收一个请求
 */
public class BioServerSingleThread {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8899)) {
            while (true) {
                System.out.println(Thread.currentThread().getId() + "-serversocket.accept");
                // accept()阻塞
                Socket socket = serverSocket.accept();
                InputStream is = socket.getInputStream();
                byte[] buf = new byte[1024];
                while (true) {
                    System.out.println(Thread.currentThread().getId() + "-inputstream.read");
                    // read()阻塞
                    if (is.read(buf) != -1) {
                        System.out.println(new String(buf, 0, buf.length));
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
