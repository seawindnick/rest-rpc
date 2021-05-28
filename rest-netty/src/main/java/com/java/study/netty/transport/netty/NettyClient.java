package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.InFlightRequests;
import com.java.study.netty.transport.Transport;
import com.java.study.netty.transport.TransportClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 22:58
 */
public class NettyClient implements TransportClient {
    private EventLoopGroup ioEventGroup;
    private Bootstrap bootstrap;
    private final InFlightRequests inFlightRequests;
    private List<Channel> channelList = new LinkedList<>();

    public NettyClient() {
        inFlightRequests = new InFlightRequests();
    }


    @Override
    public Transport createTransport(SocketAddress socketAddress, long connectionTimeout) throws TimeoutException, InterruptedException {
        return new NettyTransport(createChannel(socketAddress,connectionTimeout),inFlightRequests);
    }

    private synchronized Channel createChannel(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        if (address == null) {
            throw new IllegalArgumentException("address must not be null!");
        }

        if (ioEventGroup == null) {
            ioEventGroup = newIoEventGroup();
        }

        if (bootstrap == null) {
            ChannelHandler channelHandlerPipeline = newChannelHandlerPipeline();
            bootstrap = newBootstrap(channelHandlerPipeline, ioEventGroup);
        }

        ChannelFuture channelFuture;
        Channel channel;
        channelFuture = bootstrap.connect(address);
        if (!channelFuture.await(connectionTimeout)) {
            throw new TimeoutException();
        }

        channel = channelFuture.channel();
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException();
        }
        channelList.add(channel);
        return channel;
    }

    private ChannelHandler newChannelHandlerPipeline() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline()
                        .addLast(new ResponseDecoder())
                        .addLast(new RequestEncoder())
                        .addLast(new ResponseInvocation(inFlightRequests));
            }
        };
    }


    private EventLoopGroup newIoEventGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        }
        return new NioEventLoopGroup();
    }


    @Override
    public void close() throws IOException {
        for (Channel channel : channelList) {
            if (null != channel){
                channel.close();
            }

        }

        if (ioEventGroup != null){
            ioEventGroup.shutdownGracefully();
        }
        inFlightRequests.close();
    }

    private Bootstrap newBootstrap(ChannelHandler channelHandler, EventLoopGroup ioEventGroup) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .group(ioEventGroup)
                .handler(channelHandler)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return bootstrap;

    }
}
