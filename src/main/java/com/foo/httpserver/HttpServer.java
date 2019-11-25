package com.foo.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class HttpServer {
    public static void main(String[] args) {
        ServerBootstrap bootstrap = null;
        EventLoopGroup boss = null;
        EventLoopGroup work = null;
        int port = 8899;

        try {
            bootstrap = new ServerBootstrap();
            boss = new NioEventLoopGroup();
            work = new NioEventLoopGroup();

            bootstrap.group(boss, work)
                    .handler(new LoggingHandler(LogLevel.INFO)) // 配置handler日志级别
                    .channel(NioServerSocketChannel.class)// 配置使用的channel
                    .childHandler(new HttpServerInitializer()); // 配置初始化器
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (boss != null) {
                boss.shutdownGracefully();
            }
            if (work != null) {
                work.shutdownGracefully();
            }
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
            pipeline.addLast("httpServerCodec", new HttpServerCodec());// http 编解码
            pipeline.addLast("httpAggregator", new HttpObjectAggregator(512 * 1024)); // http 消息聚合器                                                                     512*1024为接收的最大contentlength
            pipeline.addLast("httpServerHandle", new HttpServerHandler());// 自定义请求处理器
        }
    }

    /**
     * 请求处理器，初始化器所需要的组件
     */
    private static class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            System.out.println("4 channelRead0");
            System.out.println(msg.getClass());

            if (msg instanceof HttpRequest) {

                HttpRequest req = (HttpRequest) msg; //将对象强制类型转换为HttpRequest的对象
                URI uri = new URI(req.uri()); //java.net包下的
                if ("/favicon.ico".equals(uri.getPath())) {
                    System.out.println("get favicon.icon");
                    return;
                }

                ByteBuf content = Unpooled.copiedBuffer("hello netty", CharsetUtil.UTF_8);
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                ctx.close();
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("5 channelReadComplete");
            ctx.flush();
        }

        /**
         * channel建立
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("3 channelActive");
            super.channelActive(ctx);
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("2 channelRegistered");
            super.channelRegistered(ctx);
        }

        /**
         * 连接建立
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.println("1 handlerAdded");
            super.handlerAdded(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("6 channelInactive");
            super.channelInactive(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("7 channelUnregistered");
            super.channelUnregistered(ctx);
        }
    }
}
