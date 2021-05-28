package com.java.study.netty.transport;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 18:56
 */
public interface TransportServer {
    void start(RequestHandlerRegistry requestHandlerRegistry,int port) throws InterruptedException;
    void stop();
}
