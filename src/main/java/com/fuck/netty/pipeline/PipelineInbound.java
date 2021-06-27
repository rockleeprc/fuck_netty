package com.fuck.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;

import java.io.IOException;

/**
 * 不调用super.channelRead(ctx, msg) handler不会在pipeline中向下传递
 */
public class PipelineInbound {
    public static void main(String[] args) throws IOException {
        ChannelInitializer initializer = new ChannelInitializer<EmbeddedChannel>() {
            protected void initChannel(EmbeddedChannel ch) {
                ch.pipeline().addLast(new HandlerA());
                ch.pipeline().addLast(new HandlerB());
                ch.pipeline().addLast(new HandlerC());
            }
        };

        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(1111);

        //创建嵌入式通道
        EmbeddedChannel channel = new EmbeddedChannel(initializer);
        //模拟入站，写一个入站包
        channel.writeInbound(buf);
//        channel.writeInbound(buf);
        channel.flush();
        //通道关闭
        channel.close();

        System.in.read();
    }

    private static class HandlerA extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Handler A");
            ctx.write(Unpooled.copiedBuffer("hello netty", CharsetUtil.UTF_8));
            super.channelRead(ctx, msg);
//            ctx.pipeline().remove(this);// 通过pipeline动态删除handler
        }
    }

    private static class HandlerB extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Handler B");
            super.channelRead(ctx, msg);
        }
    }

    private static class HandlerC extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Handler C");
            super.channelRead(ctx, msg);
        }
    }

}
