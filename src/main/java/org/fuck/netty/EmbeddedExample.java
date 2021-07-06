package org.fuck.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmbeddedExample {
    public static void main(String[] args) {
        ChannelInboundHandlerAdapter c1 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.debug("in c1");
                super.channelRead(ctx, msg);
            }
        };

        ChannelInboundHandlerAdapter c2 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.debug("in c2");
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello netty", CharsetUtil.UTF_8));
            }
        };

        ChannelOutboundHandlerAdapter c4 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.debug("out c4");
                super.write(ctx, msg, promise);
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(c1, c2, c4);
        embeddedChannel.writeInbound(Unpooled.copiedBuffer("hello java", CharsetUtil.UTF_8));
        embeddedChannel.writeOutbound(Unpooled.copiedBuffer("hello scala", CharsetUtil.UTF_8));
    }
}
