package com.java.study.netty.client;

import com.java.study.netty.transport.Transport;

/**
 * <Description>
 *
 * @author hushiye
 * @since 2020-12-13 23:24
 */
public interface StubFactory {
    <T> T createStub(Transport transport, Class<?> serviceClass);
}
