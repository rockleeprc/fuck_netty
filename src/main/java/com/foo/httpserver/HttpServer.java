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
        EventLoopGroup boss = null;
        EventLoopGroup work = null;
        int port = 8899;

        try {
            boss = new NioEventLoopGroup();
            work = new NioEventLoopGroup();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work)
                    .handler(new LoggingHandler(LogLevel.INFO)) // 配置handler日志级别
                    .channel(NioServerSocketChannel.class) // 配置使用的channel
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
     * 当一个新的连接被接受时，一个新的子 Channel 将会被创建，
     * 而 ChannelInitializer 将会把一个你的Handler 的实例添加到该 Channel 的 ChannelPipeline 中
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
         * 在client的连接已经建立之后将被调用
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

        /**
         * 在读取操作期间，有异常抛出时会调用
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
