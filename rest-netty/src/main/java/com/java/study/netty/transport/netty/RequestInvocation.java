package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.RequestHandler;
import com.java.study.netty.transport.RequestHandlerRegistry;
import com.java.study.netty.transport.command.Command;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 23:17
 */
@Slf4j
public class RequestInvocation extends SimpleChannelInboundHandler<Command> {
   private final RequestHandlerRegistry requestHandlerRegistry;

    public RequestInvocation(RequestHandlerRegistry requestHandlerRegistry) {
        this.requestHandlerRegistry = requestHandlerRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command request) throws Exception {
        RequestHandler handler = requestHandlerRegistry.get(request.getHeader().getType());
        if (handler != null) {
            Command response = handler.handle(request);
            if (response != null) {
                channelHandlerContext.writeAndFlush(response).addListener(
                        (ChannelFutureListener) channelFuture -> {
                            if(!channelFuture.isSuccess()){
                                log.warn("Write response failed!", channelFuture.cause());
                                channelHandlerContext.channel().close();
                            }
                        }
                );
            }else {
                log.warn("Response is null!");
            }
        }else {
            throw new Exception(String.format("No handler for request with type: %d!", request.getHeader().getType()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Exception: ", cause);
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if(channel.isActive()){
            channel.close();
        }
    }
}
