package org.fuck.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * NioEventLoopGroup 程序抛异常需要将其关闭
 * <p>
 * channel选择的交互方式NioServerSocketChannel
 * channel类型使用SocketChannel
 * <p>
 * 子channel初始化器使用ChannelInitializer
 * <p>
 * channelHandler继承ChannelInboundHandlerAdapter，实现如下方法：
 * * channelRead() 读取数据，写出数据
 * * channelReadComplete() 冲刷缓存数据，关闭channel
 * * exceptionCaught() 异常处理，关闭channel
 * <p>
 * bootstrap bind() 要同步
 * channel close() 要同步
 */
public class SimpleEchoServer {
    public static void main(String[] args) {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        InetSocketAddress localAddress = new InetSocketAddress(8888);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioServerSocketChannel.class) // 指定所使用的 NIO 传输 Channel
                    .localAddress(localAddress)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(echoServerHandler);
                        }
                    });

            // 异步地绑定服务器; 调用 sync()方法阻塞 等待直到绑定完成
            ChannelFuture channelFuture = bootstrap.bind().sync();
            // 获取 Channel 的 CloseFuture，并且阻塞当前线 程直到它完成
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //  关闭 EventLoopGroup， 释放所有的资源
            loopGroup.shutdownGracefully();
        }
    }

    @Slf4j
    @ChannelHandler.Sharable
    static class EchoServerHandler extends ChannelInboundHandlerAdapter {
        /**
         * 需要将传入消息回送给发送者，而 write()操作是异步的，直到 channelRead()方法返回后可能仍然没有完成，
         * 扩展了 ChannelInboundHandlerAdapter，其在这个时间点上不会释放消息
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            log.debug("server receive:{}", buf.toString(CharsetUtil.UTF_8));
            ctx.write(msg).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.debug("write success");
                } else {
                    log.debug("write fail");
                }
            });
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            // 将未决消息冲刷到远程节点，并且关闭该Channel
            // 这种关闭由server端主动关闭
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.getStackTrace();
            // 有异常，关闭channel
            ctx.close();
        }
    }
}
