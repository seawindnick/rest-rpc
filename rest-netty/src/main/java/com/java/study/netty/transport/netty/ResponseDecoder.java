package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.command.Command;
import com.java.study.netty.transport.netty.CommandDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 23:14
 */
public class ResponseDecoder  extends CommandDecoder {
    @Override
    protected Command.Header decodeHeader(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        int type = byteBuf.readInt();
        int version = byteBuf.readInt();
        int requestId = byteBuf.readInt();
        int code = byteBuf.readInt();
        int errorLength = byteBuf.readInt();

        byte[] errorBytes = new byte[errorLength];
        byteBuf.readBytes(errorBytes);
        String error = new String(errorBytes, StandardCharsets.UTF_8);

        return new Command.ResponseHeader(type,version,requestId,code,error);
    }
}
