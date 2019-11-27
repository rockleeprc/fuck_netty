package com.foo.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
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
        EventLoopGroup boss = null;
        EventLoopGroup work = null;
        int port = 8899;

        try {
            boss = new NioEventLoopGroup();
            work = new NioEventLoopGroup();
//            boss = new EpollEventLoopGroup();// 使用linux提供的epoll，windwos无法执行
//            work = new EpollEventLoopGroup(); // IllegalStateException: Only supported on Linux

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work)
                    .handler(new LoggingHandler(LogLevel.INFO)) // 配置handler日志级别
                    .channel(NioServerSocketChannel.class) // 配置使用的channel
//                    .channel(EpollServerSocketChannel.class) // 使用linux提供的epoll
                    .childHandler(new HttpServerInitializer()); // 配置初始化器
            ChannelFuture channelFuture = bootstrap.bind(port).sync(); // 异步调用，但是sync()阻塞等待，知道绑定完成
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
     * <p>
     * 当一个新的连接被接受时，一个新的子Channel将会被创建，
     * 而ChannelInitializer将会把一个新Handler的实例添加到该Channel的ChannelPipeline中
     */
    private static class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            // 将组建添加到ChannelPipeline的最后
            pipeline.addLast("httpServerCodec", new HttpServerCodec());// http 编解码
            pipeline.addLast("httpAggregator", new HttpObjectAggregator(512 * 1024)); // http 消息聚合器512*1024为接收的最大contentlength
            pipeline.addLast("httpServerHandle", new HttpServerHandler());// 自定义请求处理器
        }
    }

    /**
     * 请求处理器，初始化器所需要的组件
     * <p>
     * Sharable：标识一个ChannelHandler可以被多个Channel安全地共享，
     * 不然只能在ChannelInitializer中使用new HttpServerHandler()，每次都新生成一个实例
     */
    @ChannelHandler.Sharable
    private static class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        /**
         * 当接收到一条消息时被调用
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
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

        /**
         * 通知ChannelInboundHandler最后一次对channelRead()的调用是当前批量读取中的最后一条消息
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("5 channelReadComplete");
            // 远程节点，并且关闭该Channel
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        /**
         * Channel处于活动状态（已经连接到它的远程节点）。它现在可以接收和发送数据了
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("3 channelActive");
            super.channelActive(ctx);
        }

        /**
         * Channel 没有连接到远程节点
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("6 channelInactive");
            super.channelInactive(ctx);
        }

        /**
         * Channel 已经被注册到了 EventLoop
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("2 channelRegistered");
            super.channelRegistered(ctx);
        }

        /**
         * Channel 已经被创建，但还未注册到 EventLoop
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("7 channelUnregistered");
            super.channelUnregistered(ctx);
        }


        /**
         * 当把 ChannelHandler 添加到 ChannelPipeline 中时被调用
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.println("1 handlerAdded");
            super.handlerAdded(ctx);
        }

        /**
         * 当从 ChannelPipeline 中移除 ChannelHandler 时被调用
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            super.handlerRemoved(ctx);
        }


        /**
         * 当处理过程中在 ChannelPipeline 中有错误产生时被调用
         * <p>
         * 每个 Channel 都拥有一个与之相关联的 ChannelPipeline，其持有一个 ChannelHandler 的实例链。
         * 在默认的情况下，ChannelHandler 会把对它的方法的调用转发给链中的下一个 ChannelHandler。
         * 因此，如果 exceptionCaught()方法没有被该链中的某处实现，那么所接收的异常将会被传递到 ChannelPipeline 的尾端并被记录。
         * 为此，你的应用程序应该提供至少有一个实现了exceptionCaught()方法的 ChannelHandler。
         *
         * @param ctx
         * @param cause
         * @throws Exception
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
