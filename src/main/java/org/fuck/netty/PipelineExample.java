package org.fuck.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class PipelineExample {
    public static void main(String[] args) {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        InetSocketAddress localAddress = new InetSocketAddress(8888);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioServerSocketChannel.class) // 指定所使用的 NIO 传输 Channel
                    .localAddress(localAddress)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("in c1", new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            log.debug("in c1");
                                            //   调用ctx.fireChannelRead(msg) 消息才会向下一个handler传递
                                            super.channelRead(ctx, msg);
                                        }
                                    })
                                    .addLast("in c2", new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            log.debug("in c2");
                                            super.channelRead(ctx, msg);
                                        }
                                    })
                                    .addLast("in c3", new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            log.debug("in c3");
                                            // head c1 c2 c3 c4 c5 c6 tail
                                            // inbound调用顺序是 c1 c2 c3，从head开始调用
                                            // outbound调用顺序是 c6 c5 c4，从tail开始调用
                                            // 只有write才会调用ChannelOutboundHandle

                                            // channel.write从tail开始向前调用outbound
                                            //ctx.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty", CharsetUtil.UTF_8));

                                            // ctx.write是从当前handler开始向前调用outbound，c3前没有outbound
                                            ctx.writeAndFlush(Unpooled.copiedBuffer("hello netty", CharsetUtil.UTF_8));
                                        }
                                    })
                                    .addLast("out c4", new ChannelOutboundHandlerAdapter() {
                                        @Override
                                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                            log.debug("out c4");
                                            super.write(ctx, msg, promise);
                                        }
                                    })
                                    .addLast("out c5", new ChannelOutboundHandlerAdapter() {
                                        @Override
                                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                            log.debug("out c5");
                                            super.write(ctx, msg, promise);
                                        }
                                    })
                                    .addLast("out c6", new ChannelOutboundHandlerAdapter() {
                                        @Override
                                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                            log.debug("out c6");
                                            super.write(ctx, msg, promise);
                                        }
                                    });
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
}
