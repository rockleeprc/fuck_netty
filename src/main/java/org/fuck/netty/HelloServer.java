package org.fuck.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class HelloServer {
    public static void main(String[] args) {
        m2();
    }

    public static void m2() {
        // 默认不用指定线程数，NioServerSocketChannel就一个，所以只占用一个线程
        NioEventLoopGroup boss = new NioEventLoopGroup();
        // 可以指定线程数
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        // 处理耗时较长的操作，不处理IO事件
        DefaultEventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        // 指定boss、worker
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug(buf.toString(Charset.forName("UTF-8")));
                                        ctx.fireChannelRead(msg);// 发送给下一个handler处理
                                    }
                                })
                                .addLast(defaultEventLoopGroup, "default", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug(buf.toString(Charset.forName("UTF-8")));
                                    }
                                });
                    }
                }).bind(8888);
    }

    public static void m1() {
        // 组装netty组件的启动器
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 添加EventLoopGroup处理事件(accept、read)，可以设置boos、worker
        bootstrap.group(new NioEventLoopGroup())
                // ServerSocketChannel实现方式：bio、nio
                .channel(NioServerSocketChannel.class)
                // 设置worker的处理逻辑
                .childHandler(
                        // 与客户端进行读写的通道初始化器，负责添加handler
                        // 泛型是NioSocketChannel，客户端channel类型
                        new ChannelInitializer<NioSocketChannel>() {
                            // 客户端连接后调用
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline()
                                        // 添加handler
                                        .addLast(new StringDecoder()) // ByteBuf转String
                                        // 事件处理逻辑
                                        .addLast(new ChannelInboundHandlerAdapter() {
                                            @Override
                                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                                System.out.println(msg);
                                            }
                                        });
                            }
                        }).bind(8888);// 绑定端口
    }


}
