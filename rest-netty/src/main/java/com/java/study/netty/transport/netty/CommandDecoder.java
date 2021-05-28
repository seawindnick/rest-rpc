package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.command.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 18:57
 */
public abstract class CommandDecoder extends ByteToMessageDecoder {
    private static final int LENGTH_FIELD_LENGTH = Integer.BYTES;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(!byteBuf.isReadable(LENGTH_FIELD_LENGTH)){
            return;
        }
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt() - LENGTH_FIELD_LENGTH;

        if (byteBuf.readableBytes() < length){
            byteBuf.resetReaderIndex();
            return;
        }

        Command.Header header = decodeHeader(channelHandlerContext,byteBuf);
        int payloadLength = length - header.length();
        byte[] payload = new byte[payloadLength];
        byteBuf.readBytes(payload);
        list.add(new Command(header,payload));
    }


    protected abstract Command.Header decodeHeader(ChannelHandlerContext channelHandlerContext,ByteBuf byteBuf);
}
