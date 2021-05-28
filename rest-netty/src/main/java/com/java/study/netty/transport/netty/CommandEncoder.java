package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.command.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 19:57
 */
public abstract class CommandEncoder extends MessageToByteEncoder {

    //发布会数据解码
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (!(o instanceof Command)){
            throw new Exception(String.format("Unknown type: %s!", o.getClass().getCanonicalName()));
        }

        Command command = (Command) o;
        byteBuf.writeInt(Integer.BYTES + command.getHeader().length()+command.getPayload().length);
        encodeHeader(channelHandlerContext,command.getHeader(),byteBuf);
        byteBuf.writeBytes(command.getPayload());
    }


    protected void encodeHeader(ChannelHandlerContext channelHandlerContext, Command.Header header,ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(header.getType());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeInt(header.getRequestId());
    }
}
