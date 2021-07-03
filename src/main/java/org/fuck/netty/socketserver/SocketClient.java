package org.fuck.netty.socketserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class SocketClient {
    public static void main(String[] args) {
        EventLoopGroup loopGroup = null;
        try {
            loopGroup = new NioEventLoopGroup();

            // 客户端私用Bootstrap
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)// 客户端使用NioSocketChannel
                    .handler(new SocketClientInitializer());
            // 客户端使用connect()
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8899).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (loopGroup != null) {
                loopGroup.shutdownGracefully();
            }
        }
    }

    private static class SocketClientInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
            pipeline.addLast(new LengthFieldPrepender(4));
            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
            pipeline.addLast(new SocketClientHandler());
        }
    }

    private static class SocketClientHandler extends SimpleChannelInboundHandler<String> {

        /**
         * channel建立后，client先向server发送消息，才能实现ping-pong效果
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush("来自client的问候");
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(ctx.channel().remoteAddress() + "- clinet output:" + msg);
            ctx.writeAndFlush("from client:" + System.currentTimeMillis());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
