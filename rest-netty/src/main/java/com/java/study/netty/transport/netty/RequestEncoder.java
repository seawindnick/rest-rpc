package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.command.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 23:15
 */
public class RequestEncoder extends CommandEncoder {

    @Override
    protected void encodeHeader(ChannelHandlerContext channelHandlerContext, Command.Header header, ByteBuf byteBuf) throws Exception {
        super.encodeHeader(channelHandlerContext, header, byteBuf);
    }
}
