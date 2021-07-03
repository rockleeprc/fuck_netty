package org.fuck.netty.chatserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatClinet {
    public static void main(String[] args) {
        EventLoopGroup loopGroup = null;
        try {
            loopGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInitializer());

            ChannelFuture channelFutrue = bootstrap.connect("localhost", 8899).sync();
            Channel channel = channelFutrue.channel();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                channel.writeAndFlush(reader.readLine() + "\r\n");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            if (loopGroup != null) {
                loopGroup.shutdownGracefully();
            }
        }

    }

    private static class ChatClientInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()))// 解码器
                    .addLast(new StringDecoder(CharsetUtil.UTF_8))
                    .addLast(new StringEncoder(CharsetUtil.UTF_8))
                    .addLast(new ChatClientHandler());
        }
    }

    private static class ChatClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(ctx.channel().remoteAddress() + "-" + msg);
        }
    }
}
