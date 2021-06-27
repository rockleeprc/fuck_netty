package com.fuck.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;

import java.io.IOException;

/**
 * 不调用super.channelRead(ctx, msg) handler不会在pipeline中向下传递
 */
public class PipelineOutbound {
    public static void main(String[] args) throws IOException {
        ChannelInitializer initializer = new ChannelInitializer<EmbeddedChannel>() {
            protected void initChannel(EmbeddedChannel ch) {
                ch.pipeline().addLast(new HandlerA());
                ch.pipeline().addLast(new HandlerB());
                ch.pipeline().addLast(new HandlerC());
            }
        };

        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(1);

        //创建嵌入式通道
        EmbeddedChannel channel = new EmbeddedChannel(initializer);
        //模拟入站，写一个入站包
        channel.writeOutbound(buf);
        channel.flush();
        //通道关闭
        channel.close();

        System.in.read();
    }

    private static class HandlerA extends ChannelOutboundHandlerAdapter {

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("Handler A");
            super.write(ctx, msg, promise);
        }
    }

    private static class HandlerB extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("Handler B");
            super.write(ctx, msg, promise);
        }
    }

    private static class HandlerC extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("Handler C");
            super.write(ctx, msg, promise);
        }
    }

}
