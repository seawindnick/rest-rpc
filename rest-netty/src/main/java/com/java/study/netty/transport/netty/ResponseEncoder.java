package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.command.Command;
import com.java.study.netty.transport.netty.CommandEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 23:27
 */
public class ResponseEncoder extends CommandEncoder {

    @Override
    protected void encodeHeader(ChannelHandlerContext channelHandlerContext, Command.Header header, ByteBuf byteBuf) throws Exception {
        super.encodeHeader(channelHandlerContext, header, byteBuf);

        if (header instanceof Command.ResponseHeader) {
            Command.ResponseHeader responseHeader = (Command.ResponseHeader) header;
            byteBuf.writeInt(responseHeader.getCode());
            int errorLength = header.length() - (Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES);
            byteBuf.writeInt(errorLength);
            byteBuf.writeBytes(responseHeader.getError() == null ? new byte[0] : responseHeader.getError().getBytes(StandardCharsets.UTF_8));
        } else {
            throw new Exception(String.format("Invalid header type: %s!", header.getClass().getCanonicalName()));
        }
    }
}
