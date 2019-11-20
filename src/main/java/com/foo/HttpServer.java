package com.foo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class HttpServer {
    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        int prot = 8899;

        try {
            bootstrap.group(boss, work)
                    .handler(new LoggingHandler(LogLevel.DEBUG)) // 配置handler日志级别
                    .channel(NioServerSocketChannel.class)// 配置使用的channel
                    .childHandler(new HttpServerInitializer()); // 配置初始化器
            ChannelFuture f = bootstrap.bind(new InetSocketAddress(prot)).sync();
            System.out.println(" server start up on port : " + prot);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    /**
     * 初始化器，初始化所需要的组件
     */
    private static class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            // 将组建添加到ChannelPipeline的最后
            pipeline.addLast(new HttpServerCodec());// http 编解码
            pipeline.addLast("httpAggregator", new HttpObjectAggregator(512 * 1024)); // http 消息聚合器                                                                     512*1024为接收的最大contentlength
            pipeline.addLast("httpRequestHandle", new HttpRequestHandler());// 自定义请求处理器
        }
    }

    /**
     * 请求处理器，初始化器所需要的组件
     */
    private static class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            if (msg instanceof HttpRequest) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer("hello netty", CharsetUtil.UTF_8));
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }
    }
}
