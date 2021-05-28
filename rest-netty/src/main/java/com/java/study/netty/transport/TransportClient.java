package com.java.study.netty.transport;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-15 18:54
 */
public interface TransportClient extends Closeable {
    Transport createTransport(SocketAddress socketAddress,long connectionTimeout) throws TimeoutException, InterruptedException;
}
