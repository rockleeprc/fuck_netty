package org.fuck.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public class HelloClient {
    public static void main(String[] args) {
        m1();
    }

    public static void m2() {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture channelFuture = bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                // netty提供的日志调试handler
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new StringEncoder());
                    }
                }).connect(new InetSocketAddress("localhost", 8888));

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                channel.writeAndFlush("hello netty");
                channel.closeFuture();// 关闭channel
                // 只关闭channel，eventloop中的线程会继续运行，主线程无法关闭
                nioEventLoopGroup.shutdownGracefully();
            }
        });
    }

    public static void m1() {
        // 组装netty组件的启动器
        Bootstrap bootstrap = new Bootstrap();
        // 添加EventLoopGroup处理事件
        try {
            ChannelFuture channelFuture = bootstrap.group(new NioEventLoopGroup())
                    // SocketChannel实现
                    .channel(NioSocketChannel.class)
                    // 设置初始化器，添加handler
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        // 连接建立后被调用
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // netty提供的日志调试handler
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new StringEncoder());
                        }
                    })
                    // 异步非阻塞，连接server
                    .connect(new InetSocketAddress("localhost", 8888));
            Channel channel = channelFuture
                    .sync() // 阻塞方法，直到连接建立才会继续向下执行
                    .channel();
            // 与server连接的channel对象
            channel.writeAndFlush("hello,netty"); // 发送过程调用StringEncoder处理器
            // 只关闭channel无法停止主线程，需要同时关闭eventloop
            channelFuture.channel().closeFuture();
            /*
            connect：异步分阻塞方法，逻辑上需要等待连接成功后才能继续后续操作，不然获取channel可能为空
            方法一：
                sync：阻塞等待connect连接成功，获取的channel可以正常使用，目的就是为了等待connect连接成功
                main线程执行
            方法二：
                ChannelFuture.addListener(new ChannelFutureListener(){
                    operationComplete(){
                        通过channel进行后续的操作
                        nio线程执行
                    }
                })

             */
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
