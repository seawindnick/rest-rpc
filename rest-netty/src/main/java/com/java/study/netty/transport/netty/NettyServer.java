package com.java.study.netty.transport.netty;

import com.java.study.netty.transport.RequestHandlerRegistry;
import com.java.study.netty.transport.TransportServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 23:39
 */
@Slf4j
public class NettyServer implements TransportServer {

    private int port;

    private EventLoopGroup acceptEventGroup;

    private EventLoopGroup ioEventLoop;

    private Channel channel;
    private RequestHandlerRegistry requestHandlerRegistry;

    /**
     * 请求信息
     *
     * @param requestHandlerRegistry
     * @param port
     * @throws InterruptedException
     */
    @Override
    public void start(RequestHandlerRegistry requestHandlerRegistry, int port) throws InterruptedException {
        this.port = port;
        this.requestHandlerRegistry = requestHandlerRegistry;
        EventLoopGroup acceptEventGroup = newEventLoopGroup();
        EventLoopGroup ioEventGroup = newEventLoopGroup();
        ChannelHandler channelHandlerPipeline = newChannelHandlerPipeline();
        ServerBootstrap serverBootstrap = newBootstrap(channelHandlerPipeline, acceptEventGroup, ioEventGroup);
        Channel channel = doBind(serverBootstrap);

        this.acceptEventGroup = acceptEventGroup;
        this.ioEventLoop = ioEventGroup;
        this.channel = channel;
    }

    //绑定端口
    private Channel doBind(ServerBootstrap serverBootstrap) throws InterruptedException {
        return serverBootstrap.bind(port)
                .sync()
                .channel();
    }

    private ServerBootstrap newBootstrap(ChannelHandler channelHandler, EventLoopGroup acceptEventGroup, EventLoopGroup ioEventGroup) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .group(acceptEventGroup, ioEventGroup)
                .childHandler(channelHandler)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return serverBootstrap;
    }

    private ChannelHandler newChannelHandlerPipeline() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                //添加自定义处理函数
                channel.pipeline()
                        .addLast(new RequestDecoder())
                        .addLast(new ResponseEncoder())
                        .addLast(new RequestInvocation(requestHandlerRegistry));
            }
        };
    }

    private EventLoopGroup newEventLoopGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        }
        return new NioEventLoopGroup();

    }

    @Override
    public void stop() {

    }
}
