package org.fuck.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * NioEventLoopGroup
 * <p>
 * channel选择的交互方式NioSocketChannel
 * <p>
 * channel类型使用SocketChannel
 * <p>
 * channel初始化器使用ChannelInitializer
 * <p>
 * channelHandler继承SimpleChannelInboundHandler，实现如下方法：
 * channelActive() 连接建立时调用
 * channelRead0() 读取数据、自动销毁ByteBuf
 * exceptionCaught() 异常处理，关闭channel
 */
public class SimpleEchoClient {
    public static void main(String[] args) {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        EchoClientHandler echoClientHandler = new EchoClientHandler();
        InetSocketAddress remoteAddress = new InetSocketAddress(8888);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(remoteAddress)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(echoClientHandler);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(remoteAddress).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }

    @Slf4j
    @ChannelHandler.Sharable
    static class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ChannelFuture channelFuture = ctx.writeAndFlush(Unpooled.copiedBuffer("hello netty", CharsetUtil.UTF_8));
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.debug("write success");
                } else {
                    log.debug("write fail");
                }
            });
        }

        /**
         * 当 channelRead0()方法完成时，已经有了传入消息，并且已经处理完它了。
         * 当该方 法返回时，SimpleChannelInboundHandler 负责释放指向保存该消息的 ByteBuf 的内存引用
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            log.debug("client receive:{}", msg.toString(CharsetUtil.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
