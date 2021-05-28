package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.command.Command;
import com.java.study.netty.transport.netty.CommandDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 23:14
 */
public class RequestDecoder extends CommandDecoder {
    @Override
    protected Command.Header decodeHeader(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        return new Command.Header(byteBuf.readInt(),
                byteBuf.readInt(),
                byteBuf.readInt());
    }
}
