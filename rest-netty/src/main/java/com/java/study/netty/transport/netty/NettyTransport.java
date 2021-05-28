package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.InFlightRequests;
import com.java.study.netty.transport.ResponseFuture;
import com.java.study.netty.transport.command.Command;
import com.java.study.netty.transport.Transport;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 16:24
 */
public class NettyTransport implements Transport {
    private final Channel channel;
    private final InFlightRequests inFlightRequests;

    public NettyTransport(Channel channel, InFlightRequests inFlightRequests) {
        this.channel = channel;
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    public CompletableFuture<Command> send(Command request) {
        //构建返回信息
        CompletableFuture<Command> commandCompletedFuture = new CompletableFuture<>();
        try{
            //将在途请求放到 inFlightRequests 中
            inFlightRequests.put(new ResponseFuture(request.getHeader().getRequestId(),commandCompletedFuture));

            //发送命令
            channel.writeAndFlush(request).addListener((ChannelFutureListener) channelFuture->{
                //处理发送失败情况
               if (!channelFuture.isSuccess()){
                   commandCompletedFuture.completeExceptionally(channelFuture.cause());
                   channel.close();
               }
            });
        } catch (Throwable e) {
            //处理发送异常
           inFlightRequests.remove(request.getHeader().getRequestId());
           commandCompletedFuture.completeExceptionally(e);
        }
        return commandCompletedFuture;
    }
}
