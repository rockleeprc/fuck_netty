package com.foo.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 处理请求逻辑交给另外一个线程处理
 */
public class BioServer2 {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(8899)) {
            while (true) {
                System.out.println(Thread.currentThread().getId() + "-serversocket.accept");
                // accept()阻塞
                Socket socket = serverSocket.accept();
                //  另一个线程处理
                Runnable handler = () -> {
                    try (InputStream is = socket.getInputStream()) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
