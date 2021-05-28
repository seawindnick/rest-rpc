package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.InFlightRequests;
import com.java.study.netty.transport.ResponseFuture;
import com.java.study.netty.transport.command.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 23:14
 */
@Slf4j
public class ResponseInvocation extends SimpleChannelInboundHandler<Command> {

    private final InFlightRequests inFlightRequests;

    public ResponseInvocation(InFlightRequests inFlightRequests) {
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command response) throws Exception {
        ResponseFuture future = inFlightRequests.remove(response.getHeader().getRequestId());
        if (future != null) {
            future.getFuture().complete(response);
        }else {
            log.warn("Drop response: {}", response);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Exception: ", cause);
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if (channel != null){
            channel.close();
        }
    }
}
