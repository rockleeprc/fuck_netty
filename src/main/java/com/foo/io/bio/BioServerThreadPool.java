package com.foo.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BioServerThreadPool {
    public static void main(String[] args) {
        ThreadPoolExecutor threadpool = new ThreadPoolExecutor(10, 10, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10));
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
                                // 防御
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                threadpool.execute(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
